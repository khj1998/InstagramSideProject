package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * get 요청은 header에 token을 실어서 전송.
 */
public interface PostService {
    ResponseEntity<ApiResponse> AddPost(PostDto postDto);
    ResponseEntity<ApiResponse> FindPost(HttpServletRequest req,Long postId);
    ResponseEntity<ApiResponse> EditPost(PostDto postDto);
    ResponseEntity<ApiResponse> DeletePost(String postId);
    ResponseEntity<ApiResponse> AddPostLike(PostLikeDto postLikeDto);
    ResponseEntity<ApiResponse> GetMyPosts(HttpServletRequest req);
    ResponseEntity<ApiResponse> GetPostLikeList(HttpServletRequest req);
}
