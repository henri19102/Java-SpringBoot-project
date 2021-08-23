/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.Controllers;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.JpaRepositories.AccountRepository;
import projekti.JpaRepositories.MessageRepository;
import projekti.Models.Account;
import projekti.Models.Message;

/**
 *
 * @author Henri
 */
@Controller
public class MessageController {

    @Autowired
    private AccountRepository userRepo;

    @Autowired
    private MessageRepository messageRepo;

    @GetMapping("/{username}/messages")
    public String message(Model model, @PathVariable String username) {
        Account user = userRepo.findByUsername(username);
        List<Message> all = messageRepo.findAll();
        model.addAttribute("user", user);
        model.addAttribute("messages", all);

        return "message";
    }

    @PostMapping("/{username}/message")
    public String saveMessage(@PathVariable String username, @RequestParam String messagetext) {
        Account user = userRepo.findByUsername(username);
        Message message = new Message(user, messagetext, LocalDateTime.now());
        messageRepo.save(message);
        return "redirect:/{username}/messages";
    }
}
