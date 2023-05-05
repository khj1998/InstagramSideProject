package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagMappingRepository extends JpaRepository<HashTagMapping,Long> {
    Optional<HashTagMapping> findByPostIdAndHashTagId(Long postId,Long hashTagId);
}