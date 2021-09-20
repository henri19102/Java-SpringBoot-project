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
import projekti.JpaRepositories.BlockUserRepository;
import projekti.Models.BlockUser;

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
    BlockUserRepository BlockRepo;

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
    public String logIn(Model model) {
        model.addAttribute("loggedIn", null);
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
        Long id = accServ.getLoggedInUser().getId();
        List<FollowUser> all = followRepo.findAllByFollowerId(id);

        model.addAttribute("users", all);
        return "followed";
    }

    @GetMapping("/{profilePageName}")
    public String profilePage(Model model, @PathVariable String profilePageName, @RequestParam(defaultValue = "0") Integer page) {
        Account account = accountRepository.findByProfilePageName(profilePageName);

        model.addAttribute("account", account);
        model.addAttribute("messages", accServ.profilepagePagedMessages(account.getId(), page));
        model.addAttribute("msgSize", (int)Math.ceil(accServ.profilepageMessagesSize(account.getId())/5.0f));
        model.addAttribute("profilePageName", profilePageName);
        model.addAttribute("username", account.getUsername());
        model.addAttribute("image", accServ.getProfilePic(account.getId()));
        model.addAttribute("owner", accServ.isOwner(account.getId()));
        model.addAttribute("follow", accServ.listFollowedUsers(account.getId()));
        model.addAttribute("followers", accServ.listFollowingUsers());
        model.addAttribute("users", accountRepository.findAll());
        model.addAttribute("loggedIn", accServ.getLoggedInUser());

        return "profilepage";
    }

    @PostMapping("{profilePageName}/follow")
    public String post(@RequestParam String name, @PathVariable String profilePageName) {
        Account followed = accountRepository.findByUsername(name);
        accServ.follow(followed);
        return "redirect:/{profilePageName}";
    }

    @PostMapping("{profilePageName}/unfollow")
    public String unfollow(@RequestParam String name, @PathVariable String profilePageName) {
        Account followed = accountRepository.findByUsername(name);
        accServ.unFollow(followed.getId());
        return "redirect:/{profilePageName}";
    }

    @PostMapping("{profilePageName}/block")
    public String block(@RequestParam String name, @PathVariable String profilePageName) {
        Account blocked = accountRepository.findByUsername(name);
        accServ.block(blocked);
        return "redirect:/{profilePageName}";
    }

    @PostMapping("{profilePageName}/unblock")
    public String unblock(@RequestParam String name, @PathVariable String profilePageName) {
        Account blocked = accountRepository.findByUsername(name);
        accServ.unBlock(blocked);
        return "redirect:/{profilePageName}";
    }

    @PostMapping("/{profilePageName}/message")
    public String saveMessage(@PathVariable String profilePageName, @RequestParam String messagetext) {
        Account user = accountRepository.findByProfilePageName(profilePageName);
        Message message = new Message(user, messagetext, LocalDateTime.now());
        msgRepo.save(message);
        return "redirect:/{profilePageName}";
    }

    @PostMapping("/messages/{id}/like")
    public String like(@PathVariable Long id) {

        Account account = accServ.getLoggedInUser();
        Message msg = msgRepo.getOne(id);
        if (msg.getLikes().contains(account)) {
            return "redirect:/";
        }

        msg.getLikes().add(account);
        msgRepo.save(msg);

        return "redirect:/";
    }

    @PostMapping("/messages/{id}/unlike")
    public String unlike(@PathVariable Long id) {

        Account account = accServ.getLoggedInUser();
        Message msg = msgRepo.getOne(id);

        if (msg.getLikes().contains(account)) {
            msg.getLikes().remove(account);
            msgRepo.save(msg);
            return "redirect:/";
        }

        return "redirect:/";
    }

}
