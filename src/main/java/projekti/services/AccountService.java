package projekti.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Account getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return null;
        }
        return accountRepo.findByUsername(auth.getName());
    }

    public boolean isOwner(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return false;
        }
        if (accountRepo.findByUsername(auth.getName()).getId() == id) {
            return true;
        }
        return false;
    }

    public List<String> listFollowedUsers(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return null;
        }
        if (accountRepo.findByUsername(auth.getName()).getId() != id) {
            return null;
        }
        List<FollowUser> followed = followRepo.findAllByFollowerId(id);
        List<String> users = new ArrayList<>();
        followed.stream().forEach(x -> users.add(x.getFollowedUser().getUsername()));
        return users;
    }

    public List<String> listFollowingUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return null;
        }
        List<FollowUser> followers = followRepo.findAllByFollowedUserId(accountRepo.findByUsername(auth.getName()).getId());
        List<String> users = new ArrayList<>();
        followers.stream().forEach(x -> users.add(x.getFollower().getUsername()));
        return users;
    }

    public List<Message> profilepageMessages(Long id) {
        List<FollowUser> followedUsers = followRepo.findAllByFollowerId(id);
        List<Long> usersIds = new ArrayList<>();
        usersIds.add(id);

        List<Message> messages = new ArrayList<>();
        if (followedUsers.size() > 0) {
            followedUsers.stream().forEach(user -> usersIds.add(user.getFollowedUser().getId()));
            messages = msgRepo.findByAccountIdIn(usersIds);
        }
        if (followedUsers.isEmpty()) {
            messages = msgRepo.findAllByAccountId(id);
        }
        return messages;
    }

    public Image getProfilePic(Long id) {
        if (imgRepo.findByProfilePicAndAccountId(true, id) == null) {
            return null;
        }
        return imgRepo.findByProfilePicAndAccountId(true, id);
    }

    public void follow(Account followedUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return;
        }
        Account follower = accountRepo.findByUsername(auth.getName());
        if (follower == followedUser) {
            return;
        }

        if (BlockRepo.findByBlockerIdAndBlockedUserId(follower.getId(), followedUser.getId()) == null && BlockRepo.findByBlockerIdAndBlockedUserId(followedUser.getId(), follower.getId()) == null) {
            FollowUser user = new FollowUser(follower, followedUser, LocalDateTime.now());
            followRepo.save(user);
        }

    }

    public void unFollow(Long followedUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return;
        }
        FollowUser user = followRepo.findByFollowerIdAndFollowedUserId(accountRepo.findByUsername(auth.getName()).getId(), followedUserId);
        followRepo.deleteById(user.getId());
    }

    public void block(Account blockedUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return;
        }
        Account blocker = accountRepo.findByUsername(auth.getName());
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return;
        }
        Account blocker = accountRepo.findByUsername(auth.getName());
        BlockUser block = BlockRepo.findByBlockerIdAndBlockedUserId(blocker.getId(), blockedUser.getId());
        BlockRepo.deleteById(block.getId());
    }

//    public String getProfilePageName() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Account loggedInUser = accountRepo.findByUsername(auth.getName());
//        return loggedInUser.getProfilePageName();
//    }
}
