/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    @GetMapping("/{username}/images")
    public String album(Model model, @PathVariable String username) {
        Account user = accountRepo.findByUsername(username);
        List<Image> usersImages = imageRepo.findByAccountId(user.getId());
        model.addAttribute("images", usersImages);
        model.addAttribute("username", username);
        model.addAttribute("owner", accountService.isOwner(user.getId()));
        model.addAttribute("loggedIn", accountService.getLoggedInUser());
        return "images";
    }

    @GetMapping("/{username}/images/{id}")
    public String image(Model model, @PathVariable String username, @PathVariable Long id, @RequestParam(defaultValue = "0") Integer page) {
        Image img = imageRepo.getOne(id);

        model.addAttribute("image", img);
        model.addAttribute("username", username);
        model.addAttribute("size", img.getComments().size());
        model.addAttribute("pages", (int)Math.ceil(img.getComments().size()/10.0f));
        model.addAttribute("comments", img.getPagedComments(page) );
        model.addAttribute("loggedIn", accountService.getLoggedInUser());
        model.addAttribute("followerOrOwner", accountService.followerOrOwner(username));
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

        Account account = accountRepo.findByUsername(username);

        if (imageRepo.findByAccountId(account.getId()).size()>=10){
            return "redirect:/{username}/images";
        }
        Image img = new Image();

        img.setAccount(account);
        img.setName(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setContentLength(file.getSize());
        img.setContent(file.getBytes());
        img.setText(imagetext);

        imageRepo.save(img);

        return "redirect:/{username}/images";
    }

    @PostMapping("/{username}/images/{id}/comment")
    public String comment(@PathVariable String username, @PathVariable Long id, @RequestParam String comment) {

        Account account = accountService.getLoggedInUser();
        Image image = imageRepo.getOne(id);

        Comment com = new Comment();
        com.setAccount(account);
        com.setText(comment);
        commentRepo.save(com);
        image.getComments().add(com);
        imageRepo.save(image);

        return "redirect:/{username}/images/{id}";
    }

    @PostMapping("/{username}/images/{id}/like")
    public String like(@PathVariable String username, @PathVariable Long id) {

        Account account = accountService.getLoggedInUser();
        Image image = imageRepo.getOne(id);
        if(image.getLikes().contains(account)){
            return "redirect:/{username}/images";
        }
        image.getLikes().add(account);
        imageRepo.save(image);

        return "redirect:/{username}/images";
    }

    @PostMapping("/{username}/images/{id}/delete-like")
    public String deleteLike(@PathVariable String username, @PathVariable Long id) {

        Account account = accountService.getLoggedInUser();
        Image image = imageRepo.getOne(id);
        if(image.getLikes().contains(account)){
            image.getLikes().remove(account);
            imageRepo.save(image);
            return "redirect:/{username}/images";
        }
        return "redirect:/{username}/images";
    }

    @PostMapping("/{username}/images/{id}")
    public String deleteImage(@PathVariable String username, @PathVariable Long id) {
        if (Objects.equals(accountService.userLoggedIn(), username)) {
            imageRepo.deleteById(id);
            return "redirect:/{username}/images";
        }
            return "redirect:/{username}/images";
    }

    @PostMapping("/{username}/images/{id}/profile-picture")
    public String profilepic(@PathVariable String username, @PathVariable Long id, @RequestParam String profilepic) {
        if (imageRepo.findByProfilePicAndAccountId(true, accountRepo.findByUsername(username).getId()) == null) {
            Image image = imageRepo.getOne(id);
            image.setProfilePic(true);
            imageRepo.save(image);
        }
        if (imageRepo.findByProfilePicAndAccountId(true, accountRepo.findByUsername(username).getId()) != null) {
            Image pic = imageRepo.findByProfilePicAndAccountId(true, accountRepo.findByUsername(username).getId());
            pic.setProfilePic(false);
            imageRepo.save(pic);
            Image image2 = imageRepo.getOne(id);
            image2.setProfilePic(true);
            imageRepo.save(image2);
        }

        return "redirect:/{username}/images";
    }

    @GetMapping(path = "/images/{id}/content", produces = "image/jpg")
    @ResponseBody
    public byte[] get(@PathVariable Long id) {
        return imageRepo.getOne(id).getContent();
    }
}
