package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/mycomments")
    public ResponseEntity<ApiResponse> getMyComments(HttpServletRequest req) {
        List<CommentDto> commentDtoList  = commentService.GetMyComments(req);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get My Comments")
                .data(commentDtoList)
                .build();
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addComment(@RequestBody CommentDto commentDto) {
        CommentDto resDto = commentService.AddComment(commentDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Comment")
                .data(resDto)
                .build();
    }

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse> editComment(@RequestBody CommentDto commentDto) {
        CommentDto resDto = commentService.EditComment(commentDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Comment Edit")
                .data(resDto)
                .build();
    }

    @PostMapping("/comments/likes/add")
    public ResponseEntity<ApiResponse> addCommentLike(@RequestBody CommentLikeDto commentLikeDto) {
        CommentLikeDto resDto = commentService.AddCommentLike(commentLikeDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("댓글에 좋아요를 누르셨습니다.")
                .data(resDto)
                .build();
    }
}
