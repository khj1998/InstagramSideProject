package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPost(HttpServletRequest req,@RequestParam Long postId) {
        return postService.FindPost(req,postId);
    }

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
        return postService.AddPost(postDto);
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
        return postService.AddPostLike(postLikeDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deletePost(@RequestParam("postId") String postId) {
        postService.DeletePost(postId);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete postId : "+postId)
                .data(formatter.format(date))
                .build();
    }
}
