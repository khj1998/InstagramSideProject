package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.service.postservice.PostService;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @ApiOperation("게시물 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "게시물 조회 실패")
    })
    @GetMapping
    public ResponseEntity<RestApiResponse> getPost(HttpServletRequest req, @RequestParam Long postId) {
        return postService.FindPost(req,postId);
    }

    @ApiOperation("내 게시물 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "내 게시물 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "내 게시물 조회 실패")
    })
    @GetMapping("/myposts")
    public ResponseEntity<RestApiResponse> getMyPosts(HttpServletRequest req) {
        return postService.GetMyPosts(req);
    }

    @ApiOperation("게시물 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "게시물 추가 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "게시물 추가 조회 실패")
    })
    @PostMapping("/add")
    public ResponseEntity<RestApiResponse> addPost(@RequestBody PostDto postDto) {
        return postService.AddPost(postDto);
    }

    @ApiOperation("게시물 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "게시물 수정 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "게시물 수정 실패")
    })
    @PostMapping("/edit")
    public ResponseEntity<RestApiResponse> editPost(@RequestBody PostDto postDto) {
        return postService.EditPost(postDto);
    }

    @ApiOperation("좋아요 누른 게시물 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "좋아요 누른 게시물 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "좋아요 누른 게시물 조회 실패")
    })
    @GetMapping("/likes/list")
    public ResponseEntity<RestApiResponse> getPostLikeList(HttpServletRequest req) {
        return postService.GetPostLikeList(req);
    }

    @ApiOperation("게시물에 좋아요 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "게시물에 좋아요 추가 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "게시물에 좋아요 추가 실패")
    })
    @PostMapping("/likes/add")
    public ResponseEntity<RestApiResponse> addLikes(@RequestBody PostLikeDto postLikeDto) {
        return postService.AddPostLike(postLikeDto);
    }

    @ApiOperation("게시물 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "게시물 삭제 실패")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<RestApiResponse> deletePost(@RequestParam("postId") Long postId) {
        return postService.DeletePost(postId);
    }
}
