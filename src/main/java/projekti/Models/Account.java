package projekti.Models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {

    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String profilePageName;
    
    @OneToMany(mappedBy = "account")
    private List<Image> images = new ArrayList<>();
    
    public Account(String username, String password, 
            String firstName, String lastName, String profilePageName){
        this.username=username;
        this.password=password;
        this.firstName=firstName;
        this.lastName=lastName;
        this.profilePageName=profilePageName;
    }
    
}
