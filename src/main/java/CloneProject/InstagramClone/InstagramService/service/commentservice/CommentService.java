package CloneProject.InstagramClone.InstagramService.service.commentservice;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    ResponseEntity<ApiResponse> AddComment(CommentDto commentDto);
    ResponseEntity<ApiResponse> EditComment(CommentDto commentDto);
    ResponseEntity<ApiResponse> AddCommentLike(CommentLikeDto commentLikeDto);
    ResponseEntity<ApiResponse> DeleteComment(Long commentId);
    ResponseEntity<ApiResponse> GetMyComments(HttpServletRequest req);
    ResponseEntity<ApiResponse> GetMyCommentLikes(HttpServletRequest req);
}
