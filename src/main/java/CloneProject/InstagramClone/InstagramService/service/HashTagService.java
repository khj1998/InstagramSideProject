package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface HashTagService {
    ResponseEntity<ApiResponse> AddHashTagToPost(HashTagDto hashTagDto);
}
