package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<ApiResponse> AddComment(CommentDto commentDto);
    ResponseEntity<ApiResponse> EditComment(CommentDto commentDto);
    ResponseEntity<ApiResponse> AddCommentLike(CommentLikeDto commentLikeDto);
    ResponseEntity<ApiResponse> DeleteComment(String commentId);
    ResponseEntity<ApiResponse> GetMyComments(HttpServletRequest req);
    ResponseEntity<ApiResponse> GetMyCommentLikes(HttpServletRequest req);
}
