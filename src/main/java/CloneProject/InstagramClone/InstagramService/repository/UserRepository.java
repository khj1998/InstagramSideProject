package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
