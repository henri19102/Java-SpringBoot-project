package projekti.JpaRepositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.BlockUser;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {

    BlockUser findByBlockerIdAndBlockedUserId(Long blockerId, Long blockedUserId);
}
