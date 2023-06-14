package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.service.commentservice.CommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @ApiOperation("본인 댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "댓글 조회 실패")
    })
    @GetMapping("/mycomments")
    public ResponseEntity<RestApiResponse> getMyComments(HttpServletRequest req) {
        return commentService.GetMyComments(req);
    }

    @ApiOperation("댓글 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "댓글 추가 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "댓글 추가 실패")
    })
    @PostMapping("/add")
    public ResponseEntity<RestApiResponse> addComment(@RequestBody CommentDto commentDto) {
        return commentService.AddComment(commentDto);
    }

    @ApiOperation("댓글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "댓글 수정 실패")
    })
    @PostMapping("/edit")
    public ResponseEntity<RestApiResponse> editComment(@RequestBody CommentDto commentDto) {
        return commentService.EditComment(commentDto);
    }

    @ApiOperation("댓글에 좋아요 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "댓글 좋아요 추가 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "댓글 좋아요 추가 실패")
    })
    @PostMapping("/likes/add")
    public ResponseEntity<RestApiResponse> addCommentLike(@RequestBody CommentLikeDto commentLikeDto) {
        return commentService.AddCommentLike(commentLikeDto);
    }

    @ApiOperation("좋아요 누른 댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "좋아요 누른 댓글 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "좋아요 누른 댓글 조회 실패")
    })
    @GetMapping("/likes")
    public ResponseEntity<RestApiResponse> getCommentLikeList(HttpServletRequest req) {
        return commentService.GetMyCommentLikes(req);
    }

    @ApiOperation("댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode= "200",description="댓글 삭제 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "댓글 삭제 실패")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<RestApiResponse> deleteComment(@RequestParam("commentId") Long commentId) {
        return commentService.DeleteComment(commentId);
    }
}
