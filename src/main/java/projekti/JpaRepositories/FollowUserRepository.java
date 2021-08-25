package projekti.JpaRepositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.FollowUser;

public interface FollowUserRepository extends JpaRepository<FollowUser, Long> {

    List<FollowUser> findAllByFollowerId(Long accountId);
}
