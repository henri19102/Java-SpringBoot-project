package projekti.JpaRepositories;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
