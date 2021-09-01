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
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueBlockerAndBlocked", columnNames = {"blocked_user_id", "blocker_id"})})
public class BlockUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Account blocker;
    @ManyToOne
    private Account blockedUser;

    public BlockUser(Account blocker, Account blockedUser) {
        this.blocker = blocker;
        this.blockedUser = blockedUser;
    }

}
