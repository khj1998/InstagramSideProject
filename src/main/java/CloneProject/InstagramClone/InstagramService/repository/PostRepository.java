package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
}
