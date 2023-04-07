package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/myposts")
    public ResponseEntity<ApiResponse> getMyPosts(HttpServletRequest req) {
        List<PostDto> resDtoList = postService.GetMyPosts(req);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 작성한 게시물들 리스트")
                .data(resDtoList)
                .build();
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPost(@RequestBody PostDto postDto) {
        PostDto resDto = postService.AddPost(postDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Post")
                .data(resDto)
                .build();
    }

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse> getPost(@RequestBody PostDto postDto) {
        PostDto resDto = postService.EditPost(postDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("edited post number : "+postDto.getId())
                .data(resDto)
                .build();
    }

    @PostMapping("/comments/add")
    public ResponseEntity<ApiResponse> addComment(@RequestBody CommentDto commentDto) {
        CommentDto resDto = postService.AddComment(commentDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Comment")
                .data(resDto)
                .build();
    }

    @GetMapping("/likes/list")
    public ResponseEntity<ApiResponse> getLikeList(HttpServletRequest req) {
        List<PostDto> resDtoList = postService.GetPostLikeList(req);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 좋아요를 누른 글 리스트")
                .data(resDtoList)
                .build();
    }

    @PostMapping("/likes/add")
    public ResponseEntity<ApiResponse> addLikes(@RequestBody PostLikeDto postLikeDto) {
        PostLikeDto resDto = postService.AddPostLike(postLikeDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message(resDto.getPostTitle()+" 글에 좋아요를 등록하였습니다.")
                .data(resDto)
                .build();
    }

    @PostMapping("/comments/likes/add")
    public ResponseEntity<ApiResponse> addCommentLike(@RequestBody CommentLikeDto commentLikeDto) {
        CommentLikeDto resDto = postService.AddCommentLike(commentLikeDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("댓글에 좋아요를 누르셨습니다.")
                .data(resDto)
                .build();
    }
}
