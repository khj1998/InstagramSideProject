package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
}
