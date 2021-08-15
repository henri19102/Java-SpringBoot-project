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
import projekti.JpaRepositories.ImageRepo;
import projekti.Models.Account;
import projekti.Models.Image;
import projekti.services.AccountService;

@Controller
public class ImageController {

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private AccountService accountService;

    @GetMapping("/images")
    public String files(Model model) {
        model.addAttribute("images", imageRepo.findAll());
        model.addAttribute("userId", accountService.getLoggedInUserId());
        model.addAttribute("isUserLoggedIn", accountService.isUserLoggedIn());
        return "images";
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

    @PostMapping("/images/{accountId}")
    public String save(@RequestParam("image") MultipartFile file, @PathVariable Long accountId) throws IOException {
        Image img = new Image();

        Account account = accountRepo.getOne(accountId);

        img.setAccount(account);
        img.setName(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setContentLength(file.getSize());
        img.setContent(file.getBytes());

        imageRepo.save(img);

        //  account.getImages().add(img);
        //  accountRepo.save(account);
        return "redirect:/images";
    }

    @GetMapping(path = "/images/{id}/content", produces = "image/jpg")
    @ResponseBody
    public byte[] get(@PathVariable Long id) {
        return imageRepo.getOne(id).getContent();
    }
}
