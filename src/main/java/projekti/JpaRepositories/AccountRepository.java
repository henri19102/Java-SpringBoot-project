package projekti.JpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);

    Account findByProfilePageName(String profilePageName);

}
