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
import projekti.Models.FollowUser;

/**
 *
 * @author Henri
 */
@Controller
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    FollowUserRepository followRepo;

    @Autowired
    ImageRepository imgRepo;

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

    @GetMapping("/users/{profilePageName}")
    public String profilePage(Model model, @PathVariable String profilePageName) {
        Account account = accountRepository.findByProfilePageName(profilePageName);
        List<Image> usersImages = imgRepo.findByAccountId(account.getId());
        model.addAttribute("account", account);

        model.addAttribute("images", usersImages);

        return "profilepage";
    }

    @PostMapping("/users/{username}/follow")
    public String post(@RequestParam String name, @PathVariable String username) {
        Account followed = accountRepository.findByUsername(name);
        Account follower = accountRepository.findByUsername(username);
        FollowUser user = new FollowUser(follower, followed, LocalDateTime.now());
        followRepo.save(user);

        return "redirect:/users";
    }

}
