package CloneProject.InstagramClone.InstagramService.repository;

import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagMappingRepository extends JpaRepository<HashTagMapping,Long> {
}