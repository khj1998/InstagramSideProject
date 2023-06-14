package CloneProject.InstagramClone.InstagramService.service.postservice;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;

/**
 * get 요청은 header에 token을 실어서 전송.
 */
public interface PostService {
    ResponseEntity<RestApiResponse> AddPost(PostDto postDto);
    ResponseEntity<RestApiResponse> FindPost(HttpServletRequest req, Long postId);
    ResponseEntity<RestApiResponse> EditPost(PostDto postDto);
    ResponseEntity<RestApiResponse> DeletePost(Long postId);
    ResponseEntity<RestApiResponse> AddPostLike(PostLikeDto postLikeDto);
    ResponseEntity<RestApiResponse> GetMyPosts(HttpServletRequest req);
    ResponseEntity<RestApiResponse> GetPostLikeList(HttpServletRequest req);
}
