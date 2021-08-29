/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.Controllers;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.Models.Account;
import projekti.JpaRepositories.AccountRepository;
import projekti.JpaRepositories.FollowUserRepository;
import projekti.Models.Image;
import projekti.services.AccountService;
import projekti.JpaRepositories.ImageRepository;
import projekti.JpaRepositories.MessageRepository;
import projekti.Models.FollowUser;
import projekti.Models.Message;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 *
 * @author Henri
 */
@Controller
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accServ;

    @Autowired
    FollowUserRepository followRepo;

    @Autowired
    ImageRepository imgRepo;

    @Autowired
    MessageRepository msgRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password,
            @RequestParam String lastName, @RequestParam String firstName,
            @RequestParam String profilePageName) {
        if (accountRepository.findByUsername(username) != null) {
            return "redirect:/login";
        }

        Account a = new Account(username, passwordEncoder.encode(password), firstName,
                lastName, profilePageName);
        accountRepository.save(a);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String logIn() {
        return "login";
    }

    @GetMapping("/user/{username}")
    public String users2(Model model, @PathVariable String username) {
        List<Account> all = accountRepository.findAll();
        Account user = accountRepository.findByUsername(username);
        model.addAttribute("users", all);
        model.addAttribute("user", user);
        return "users";
    }

    @GetMapping("/user/followed")
    public String users3(Model model) {
        Long id = accServ.getLoggedInUserId();
        List<FollowUser> all = followRepo.findAllByFollowerId(id);

        model.addAttribute("users", all);
        return "followed";
    }

    @GetMapping("/{profilePageName}")
    public String profilePage(Model model, @PathVariable String profilePageName) {

        Account account = accountRepository.findByProfilePageName(profilePageName);

        boolean owner = account == accServ.getAccount() ? true : false;

        List<Image> usersImages = imgRepo.findByAccountId(account.getId());
        List<FollowUser> followedUsers = followRepo.findAllByFollowerId(account.getId());
        List<Long> usersIds = new ArrayList<>();
        usersIds.add(account.getId());

        List<Message> messages = new ArrayList<>();
        if (followedUsers.size() > 0) {
            followedUsers.stream().forEach(user -> usersIds.add(user.getFollowedUser().getId()));
            messages = msgRepo.findByAccountIdIn(usersIds);
        }
        if (followedUsers.isEmpty()) {
            messages = msgRepo.findAllByAccountId(account.getId());
        }
        Image newImg = new Image();
        if (usersImages.isEmpty()) {
            newImg = null;
        }
        if (usersImages.size() > 0) {
            newImg = usersImages.get(0);
        }

        model.addAttribute("account", account);
        model.addAttribute("messages", messages);
        model.addAttribute("profilePageName", profilePageName);
        model.addAttribute("username", account.getUsername());
        model.addAttribute("image", newImg);
        model.addAttribute("owner", owner);

        return "profilepage";
    }

    @PostMapping("/users/{username}/follow")
    public String post(@RequestParam String name, @PathVariable String username) {
        Account followed = accountRepository.findByUsername(name);
        Account follower = accServ.getAccount();
        if (followed == follower) {
            return "redirect:/login";
        }
        FollowUser user = new FollowUser(follower, followed, LocalDateTime.now());
        followRepo.save(user);

        return "redirect:/user/{username}";
    }

    @PostMapping("/{profilePageName}/message")
    public String saveMessage(@PathVariable String profilePageName, @RequestParam String messagetext) {
        Account user = accountRepository.findByProfilePageName(profilePageName);
        Message message = new Message(user, messagetext, LocalDateTime.now());
        msgRepo.save(message);
        return "redirect:/{profilePageName}";
    }

}
