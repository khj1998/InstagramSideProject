package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    CommentDto AddComment(CommentDto commentDto);
    CommentDto EditComment(CommentDto commentDto);
    ResponseEntity<ApiResponse> AddCommentLike(CommentLikeDto commentLikeDto);
    void DeleteComment(String commentId);
    List<CommentDto> GetMyComments(HttpServletRequest req);
    List<CommentDto> GetMyCommentLikes(HttpServletRequest req);
}
