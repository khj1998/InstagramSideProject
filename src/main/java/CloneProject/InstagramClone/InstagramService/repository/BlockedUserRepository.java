package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedUserRepository extends JpaRepository<BlockedUser,Long> {
    Optional<BlockedUser> findByFromBlockedIdAndToBlockedId(Long fromBlockedId, Long toBlockedId);
}
