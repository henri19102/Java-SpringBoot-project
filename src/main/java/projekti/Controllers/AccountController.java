/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.Controllers;

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
import projekti.JpaRepositories.ImageRepo;
import projekti.Models.Image;
import projekti.services.AccountService;

/**
 *
 * @author Henri
 */
@Controller
public class AccountController {

    @Autowired
    AccountRepository accountRepository;



    @Autowired
    ImageRepo imgRepo;

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
    
        @GetMapping("/users")
    public String users(Model model) {
        List<Account> all = accountRepository.findAll();
        model.addAttribute("users", all);
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

}
