package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * get 요청은 header에 token을 실어서 전송.
 */
public interface PostService {
    PostDto AddPost(PostDto postDto);
    CommentDto AddComment(CommentDto commentDto);
    PostLikeDto AddPostLike(PostLikeDto postLikeDto);
    List<PostDto> getMyPosts(HttpServletRequest req);
    List<PostDto> getPostLikeList(HttpServletRequest req);
}