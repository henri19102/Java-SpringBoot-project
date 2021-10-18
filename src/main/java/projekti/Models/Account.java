package projekti.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String profilePageName;

    @OneToMany(mappedBy = "account")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Message> messages;
    @OneToMany(mappedBy = "follower")
    private List<FollowUser> followUsers;

    public Account(String username, String password,
            String firstName, String lastName, String profilePageName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePageName = profilePageName;
    }

}
