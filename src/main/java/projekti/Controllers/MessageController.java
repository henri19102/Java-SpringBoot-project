/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.Controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.JpaRepositories.AccountRepository;
import projekti.JpaRepositories.CommentRepository;
import projekti.JpaRepositories.FollowUserRepository;
import projekti.JpaRepositories.MessageRepository;
import projekti.Models.Account;
import projekti.Models.Comment;
import projekti.Models.Message;
import projekti.services.AccountService;

/**
 * @author Henri
 */
@Controller
public class MessageController {

    @Autowired
    private AccountRepository userRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private FollowUserRepository followRepo;

    @Autowired
    private AccountService accServ;

    @GetMapping("/{username}/messages")
    public String messages(Model model, @PathVariable String username) {
        Account user = userRepo.findByUsername(username);
        List<Message> all = messageRepo.findAll();
        model.addAttribute("username", username);
        model.addAttribute("messages", all);
        model.addAttribute("isUserLoggedIn", accServ.getLoggedInUser());

        return "messages";
    }

    @GetMapping("/{username}/messages/{id}")
    public String message(Model model, @PathVariable String username, @PathVariable Long id, @RequestParam(defaultValue = "0") Integer page) {

        Account user = userRepo.findByUsername(username);
        Message msg = messageRepo.getOne(id);

        model.addAttribute("username", username);
        model.addAttribute("msg", msg);
        model.addAttribute("auth", accServ.getLoggedInUser());
        model.addAttribute("size", msg.getComments().size());
        model.addAttribute("pages", (int) Math.ceil(msg.getComments().size() / 10.0f));
        model.addAttribute("comments", msg.getPagedComments(page));
        model.addAttribute("loggedIn", accServ.getLoggedInUser());
        model.addAttribute("followerOrOwner", accServ.followerOrOwner(username));
        model.addAttribute("joku", accServ.joku());


        return "message";
    }

    @PostMapping("/{username}/messages/{id}/comment")
    public String comment(@PathVariable String username, @PathVariable Long id, @RequestParam String comment) {

        Account account = accServ.getLoggedInUser();
        Message msg = messageRepo.getOne(id);

        Comment com = new Comment();
        com.setAccount(account);
        com.setText(comment);
        commentRepo.save(com);
        msg.getComments().add(com);
        messageRepo.save(msg);

        return "redirect:/{username}/messages/{id}";
    }

    @PostMapping("/{username}/messages/{id}/like")
    public String like(@PathVariable String username, @PathVariable Long id) {

        Account account = accServ.getLoggedInUser();
        Message msg = messageRepo.getOne(id);
        if (msg.getLikes().contains(account)) {
            return "redirect:/{username}/images";
        }

        msg.getLikes().add(account);
        messageRepo.save(msg);

        return "redirect:/{username}/images";
    }

    @PostMapping("/{username}/messages/{id}/unlike")
    public String unlike(@PathVariable String username, @PathVariable Long id) {

        Account account = accServ.getLoggedInUser();
        Message msg = messageRepo.getOne(id);

        if (msg.getLikes().contains(account)) {
            msg.getLikes().remove(account);
            messageRepo.save(msg);
            return "redirect:/{username}/images";
        }

        return "redirect:/{username}/images";
    }

//    @PostMapping("/{username}/message")
//    public String saveMessage(@PathVariable String username, @RequestParam String messagetext) {
//        Account user = userRepo.findByUsername(username);
//        Message message = new Message(user, messagetext, LocalDateTime.now());
//        messageRepo.save(message);
//        return "redirect:/{username}/messages";
//    }
}
