package projekti.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekti.JpaRepositories.AccountRepository;
import projekti.Models.Account;
import projekti.services.AccountService;

@Controller
public class DefaultController {

    @Autowired
    private AccountRepository acc;
    
    @Autowired
    private AccountService serv;

    @GetMapping("*")
    public String helloWorld(Model model) {
        return "index";
    }
}
