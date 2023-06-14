package CloneProject.InstagramClone.InstagramService.service.hashtagservice;

import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface HashTagService {
    ResponseEntity<RestApiResponse> GetHashTag(HttpServletRequest req, Long id);
    ResponseEntity<RestApiResponse> GetPopularHashTag();
}
