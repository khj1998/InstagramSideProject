package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long> {
    Optional<Friend> findByToMemberIdAndFromMemberId(Long fromMemberId, Long toMemberId);
}
