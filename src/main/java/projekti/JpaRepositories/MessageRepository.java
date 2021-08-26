package projekti.JpaRepositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByAccountId(Long accountId);
}
