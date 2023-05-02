package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.member.BlockedMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedMemberRepository extends JpaRepository<BlockedMember,Long> {
    Optional<BlockedMember> findByFromMemberIdAndToMemberId(Long fromBlockedId, Long toBlockedId);
}
