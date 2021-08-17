package projekti.JpaRepositories;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
