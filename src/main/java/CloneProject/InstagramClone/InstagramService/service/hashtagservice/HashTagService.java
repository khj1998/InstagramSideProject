package CloneProject.InstagramClone.InstagramService.service.hashtagservice;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface HashTagService {
    ResponseEntity<ApiResponse> GetHashTag(HttpServletRequest req, String hashTagName);
    ResponseEntity<ApiResponse> GetPopularHashTag(HttpServletRequest req);
}
