package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface HashTagService {
    ResponseEntity<ApiResponse> GetHashTag(HttpServletRequest req,String hashTagName);
}
