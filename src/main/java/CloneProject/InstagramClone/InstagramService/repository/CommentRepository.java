package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
