package projekti.Models;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueFollowerAndFollowed", columnNames = {"followed_user_id", "follower_id"})})
public class FollowUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Account follower;
    @ManyToOne
    private Account followedUser;
    private LocalDateTime followStartTime;
    private boolean block;
    
    public FollowUser(Account follower, Account followed, LocalDateTime time){
        this.followedUser=followed;
        this.follower=follower;
        this.followStartTime=time;
    }


}
