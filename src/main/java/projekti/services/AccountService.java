package projekti.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.JpaRepositories.AccountRepository;
import projekti.JpaRepositories.BlockUserRepository;
import projekti.JpaRepositories.FollowUserRepository;
import projekti.JpaRepositories.ImageRepository;
import projekti.JpaRepositories.MessageRepository;
import projekti.Models.Account;
import projekti.Models.BlockUser;
import projekti.Models.FollowUser;
import projekti.Models.Image;
import projekti.Models.Message;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private FollowUserRepository followRepo;

    @Autowired
    MessageRepository msgRepo;

    @Autowired
    ImageRepository imgRepo;

    @Autowired
    BlockUserRepository BlockRepo;

    public String userLoggedIn() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.getName() != null) {
                return auth.getName();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Account getLoggedInUser() {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return null;
        }
        return accountRepo.findByUsername(userLoggedIn());
    }

    public boolean isOwner(Long id) {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return false;
        }
        if (accountRepo.findByUsername(userLoggedIn()).getId() == id) {
            return true;
        }
        return false;
    }

    public List<Account> listFollowedUsers(Long id) {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return null;
        }
        if (accountRepo.findByUsername(userLoggedIn()).getId() != id) {
            return null;
        }
        List<FollowUser> followed = followRepo.findAllByFollowerId(id);
        List<Account> users = new ArrayList<>();
        followed.stream().forEach(x -> users.add(accountRepo.findByUsername(x.getFollowedUser().getUsername())));
        return users;
    }

    public List<Account> listFollowingUsers() {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return null;
        }
        List<FollowUser> followers = followRepo.findAllByFollowedUserId(accountRepo.findByUsername(userLoggedIn()).getId());
        List<Account> users = new ArrayList<>();
        followers.stream().forEach(x -> users.add(accountRepo.findByUsername(x.getFollower().getUsername())));
        return users;
    }

    public int profilepageMessagesSize(Long id) {
        List<FollowUser> followedUsers = followRepo.findAllByFollowerId(id);
        List<Long> usersIds = new ArrayList<>();
        usersIds.add(id);

        int msgSize = 0;
        if (followedUsers.size() > 0) {
            followedUsers.stream().forEach(user -> usersIds.add(user.getFollowedUser().getId()));
            msgSize = msgRepo.countByAccountIdIn(usersIds);
        }
        if (followedUsers.isEmpty()) {
            msgSize = msgRepo.countByAccountId(id);
        }
        return msgSize;
    }

    public List<Message> profilepagePagedMessages(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("sendTime").descending());
        List<FollowUser> followedUsers = followRepo.findAllByFollowerId(id);
        List<Long> usersIds = new ArrayList<>();
        usersIds.add(id);

        if (followedUsers.size() > 0) {
            followedUsers.stream().forEach(user -> usersIds.add(user.getFollowedUser().getId()));
            return msgRepo.findByAccountIdIn(usersIds, pageable);
        }
        return msgRepo.findAllByAccountId(id, pageable);
    }

    public Image getProfilePic(Long id) {
        if (imgRepo.findByProfilePicAndAccountId(true, id) == null) {
            return null;
        }
        return imgRepo.findByProfilePicAndAccountId(true, id);
    }

    public void follow(Account followedUser) {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return;
        }
        Account follower = accountRepo.findByUsername(userLoggedIn());
        if (follower == followedUser) {
            return;
        }

        if (BlockRepo.findByBlockerIdAndBlockedUserId(follower.getId(), followedUser.getId()) == null && BlockRepo.findByBlockerIdAndBlockedUserId(followedUser.getId(), follower.getId()) == null) {
            FollowUser user = new FollowUser(follower, followedUser, LocalDateTime.now());
            followRepo.save(user);
        }

    }

    public void unFollow(Long followedUserId) {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return;
        }
        FollowUser user = followRepo.findByFollowerIdAndFollowedUserId(accountRepo.findByUsername(userLoggedIn()).getId(), followedUserId);
        followRepo.deleteById(user.getId());
    }

    public void block(Account blockedUser) {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return;
        }
        Account blocker = accountRepo.findByUsername(userLoggedIn());
        BlockUser block = new BlockUser(blocker, blockedUser);
        BlockRepo.save(block);
        if (followRepo.findByFollowerIdAndFollowedUserId(blocker.getId(), blockedUser.getId()) != null) {
            FollowUser user = followRepo.findByFollowerIdAndFollowedUserId(blocker.getId(), blockedUser.getId());
            followRepo.deleteById(user.getId());
        }
        if (followRepo.findByFollowerIdAndFollowedUserId(blockedUser.getId(), blocker.getId()) != null) {
            FollowUser user = followRepo.findByFollowerIdAndFollowedUserId(blockedUser.getId(), blocker.getId());
            followRepo.deleteById(user.getId());
        }
    }

    public void unBlock(Account blockedUser) {

        if (accountRepo.findByUsername(userLoggedIn()) == null) {
            return;
        }
        Account blocker = accountRepo.findByUsername(userLoggedIn());
        BlockUser block = BlockRepo.findByBlockerIdAndBlockedUserId(blocker.getId(), blockedUser.getId());
        BlockRepo.deleteById(block.getId());
    }

    public boolean followerOrOwner(String username) {
        if (Objects.equals(userLoggedIn(), username)) {
            return true;
        }
        Account follower = accountRepo.findByUsername(userLoggedIn());
        Account followed = accountRepo.findByUsername(username);
        if (followRepo.findByFollowerIdAndFollowedUserId(follower.getId(), followed.getId()) == null) {
            return false;
        }
        return true;
    }
}
