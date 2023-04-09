package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.Follow;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    Long countByFollower(Member follower);
    Long countByFollowing(Member following);
}
