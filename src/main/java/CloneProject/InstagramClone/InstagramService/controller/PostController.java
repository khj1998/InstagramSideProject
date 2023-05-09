package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPost(HttpServletRequest req, @RequestParam Long postId) {
        return postService.FindPost(req,postId);
    }

    @GetMapping("/myposts")
    public ResponseEntity<ApiResponse> getMyPosts(HttpServletRequest req) {
        return postService.GetMyPosts(req);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPost(@RequestBody PostDto postDto) {
        return postService.AddPost(postDto);
    }

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse> getPost(@RequestBody PostDto postDto) {
        return postService.EditPost(postDto);
    }

    @GetMapping("/likes/list")
    public ResponseEntity<ApiResponse> getLikeList(HttpServletRequest req) {
        return postService.GetPostLikeList(req);
    }

    @PostMapping("/likes/add")
    public ResponseEntity<ApiResponse> addLikes(@RequestBody PostLikeDto postLikeDto) {
        return postService.AddPostLike(postLikeDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deletePost(@RequestParam("postId") Long postId) {
        return postService.DeletePost(postId);
    }
}
