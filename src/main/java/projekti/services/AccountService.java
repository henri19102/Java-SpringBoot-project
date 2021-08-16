package projekti.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.JpaRepositories.AccountRepository;
import projekti.Models.Account;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    public Long getLoggedInUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account loggedInUser = accountRepo.findByUsername(auth.getName());
        return loggedInUser.getId();
    }

    public boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (accountRepo.findByUsername(auth.getName()) == null) {
            return false;
        }
        return true;

    }

  
//    public String getProfilePageName() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Account loggedInUser = accountRepo.findByUsername(auth.getName());
//        return loggedInUser.getProfilePageName();
//    }
 
}
