package CloneProject.InstagramClone.InstagramService.service.commentservice;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    ResponseEntity<RestApiResponse> AddComment(CommentDto commentDto);
    ResponseEntity<RestApiResponse> EditComment(CommentDto commentDto);
    ResponseEntity<RestApiResponse> AddCommentLike(CommentLikeDto commentLikeDto);
    ResponseEntity<RestApiResponse> DeleteComment(Long commentId);
    ResponseEntity<RestApiResponse> GetMyComments(HttpServletRequest req);
    ResponseEntity<RestApiResponse> GetMyCommentLikes(HttpServletRequest req);
}
