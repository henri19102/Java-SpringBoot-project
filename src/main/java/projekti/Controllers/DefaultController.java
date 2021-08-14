package projekti.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("*")
    public String helloWorld(Model model) {
        model.addAttribute("message", "World!");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        model.addAttribute("auth", auth.getAuthorities());
        return "index";
    }
}
