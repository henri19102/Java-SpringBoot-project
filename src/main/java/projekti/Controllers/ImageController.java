/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.Controllers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import projekti.JpaRepositories.AccountRepository;
import projekti.JpaRepositories.CommentRepository;
import projekti.Models.Account;
import projekti.Models.Image;
import projekti.services.AccountService;
import projekti.JpaRepositories.ImageRepository;
import projekti.Models.Comment;

@Controller
public class ImageController {

    @Autowired
    private ImageRepository imageRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private AccountService accountService;

    @GetMapping("/images")
    public String files(Model model) {
        model.addAttribute("images", imageRepo.findAll());
        model.addAttribute("userId", accountService.getLoggedInUserId());
        model.addAttribute("isUserLoggedIn", accountService.isUserLoggedIn());
        return "images";
    }

    @GetMapping("/{username}/images")
    public String album(Model model, @PathVariable String username) {
        Account user = accountRepo.findByUsername(username);
        model.addAttribute("images", imageRepo.findByAccountId(user.getId()));
        model.addAttribute("username", username);
        model.addAttribute("isUserLoggedIn", accountService.isUserLoggedIn());
        return "images";
    }

    @GetMapping("/{username}/images/{id}")
    public String image(Model model, @PathVariable String username, @PathVariable Long id) {
        Account user = accountRepo.findByUsername(username);
        Image img = imageRepo.getOne(id);
        model.addAttribute("image", img);
        model.addAttribute("username", username);
        model.addAttribute("comments", img.getComments());

        return "image";
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> viewFile(@PathVariable Long id) {
        Image img = imageRepo.getOne(id);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(img.getContentType()));
        headers.setContentLength(img.getContentLength());
        headers.add("Content-Disposition", "attachment; filename=" + img.getName());

        return new ResponseEntity<>(img.getContent(), headers, HttpStatus.CREATED);
    }

    @PostMapping("/{username}/images")
    public String save(@RequestParam("image") MultipartFile file, @PathVariable String username, @RequestParam String imagetext) throws IOException {
        Image img = new Image();

        Account account = accountRepo.findByUsername(username);

        img.setAccount(account);
        img.setName(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setContentLength(file.getSize());
        img.setContent(file.getBytes());
        img.setText(imagetext);

        imageRepo.save(img);

        //  account.getImages().add(img);
        //  accountRepo.save(account);
        return "redirect:/{username}/images";
    }

    @PostMapping("/{username}/images/{id}/comment")
    public String comment(@PathVariable String username, @PathVariable Long id, @RequestParam String comment) {

        Account account = accountRepo.findByUsername(username);
        Image image = imageRepo.getOne(id);
        
        Comment com = new Comment();
        com.setAccount(account);
        com.setText(comment);
        commentRepo.save(com);
        image.getComments().add(com);
        imageRepo.save(image);

        return "redirect:/{username}/images";
    }

    @GetMapping(path = "/images/{id}/content", produces = "image/jpg")
    @ResponseBody
    public byte[] get(@PathVariable Long id) {
        return imageRepo.getOne(id).getContent();
    }
}
