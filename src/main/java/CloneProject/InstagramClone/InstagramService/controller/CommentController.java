package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.commentservice.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/mycomments")
    public ResponseEntity<ApiResponse> getMyComments(HttpServletRequest req) {
        return commentService.GetMyComments(req);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addComment(@RequestBody CommentDto commentDto) {
        return commentService.AddComment(commentDto);
    }

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse> editComment(@RequestBody CommentDto commentDto) {
        return commentService.EditComment(commentDto);
    }

    @PostMapping("/likes/add")
    public ResponseEntity<ApiResponse> addCommentLike(@RequestBody CommentLikeDto commentLikeDto) {
        return commentService.AddCommentLike(commentLikeDto);
    }

    @GetMapping("/likes")
    public ResponseEntity<ApiResponse> getCommentLikeList(HttpServletRequest req) {
        return commentService.GetMyCommentLikes(req);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteComment(@RequestParam("commentId") String commentId) {
        return commentService.DeleteComment(commentId);
    }
}
