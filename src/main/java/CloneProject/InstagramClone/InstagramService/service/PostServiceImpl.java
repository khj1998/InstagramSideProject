package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.entity.*;
import CloneProject.InstagramClone.InstagramService.exception.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.repository.*;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final CommentLikeRepository commentLikeRepository;

    private final ModelMapper modelMapper;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    public PostDto AddPost(PostDto postDto) {
        Post postEntity = modelMapper.map(postDto,Post.class);
        Member memberEntity = findMember(postDto.getAccessToken());
        postEntity.setMember(memberEntity);
        memberEntity.getPostList().add(postEntity);

        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        PostDto response = modelMapper.map(postEntity, PostDto.class);
        response.setAccessToken(postDto.getAccessToken());
        return response;
    }

    /**
     * 댓글 쓴 Member, 댓글 - Member, 글 - 댓글 연관관계 매핑
     */
    @Override
    public CommentDto AddComment(CommentDto commentDto) {
        Post postEntity = findPost(commentDto.getPostId());
        Member memberEntity = findMember(commentDto.getAccessToken());
        Comment commentEntity = modelMapper.map(commentDto, Comment.class);

        commentEntity.setPost(postEntity);
        commentEntity.setMember(memberEntity);
        postEntity.getCommentList().add(commentEntity);
        memberEntity.getCommentList().add(commentEntity);

        commentRepository.save(commentEntity);
        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        return modelMapper.map(commentEntity, CommentDto.class);
    }

    /**
     * 이미 좋아요를 추가한 상태라면 좋아요 취소
     */
    @Override
    public PostLikeDto AddPostLike(PostLikeDto postLikeDto) {
        Member memberEntity = findMember(postLikeDto.getAccessToken());
        Post postEntity = findPost(postLikeDto.getPostId());
        PostLike postLikeEntity = new PostLike();

        postLikeEntity.setMember(memberEntity);
        postLikeEntity.setPost(postEntity);
        memberEntity.getPostLikeList().add(postLikeEntity);
        postEntity.getPostLikeList().add(postLikeEntity);

        postLikeRepository.save(postLikeEntity);
        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        postLikeDto.setPostTitle(postEntity.getTitle());
        return postLikeDto;
    }

    @Override
    public CommentLikeDto AddCommentLike(CommentLikeDto commentLikeDto) {
        Long commentId = commentLikeDto.getCommentId();
        CommentLike commentLike = new CommentLike();
        Comment commentEntity = findComment(commentId);
        Member memberEntity = findMember(commentLikeDto.getAccessToken());

        commentLike.setComment(commentEntity);
        commentLike.setMember(memberEntity);
        commentEntity.getCommentLikeList().add(commentLike);

        commentLikeRepository.save(commentLike);
        commentRepository.save(commentEntity);

        Comment testComment = findComment(commentLikeDto.getCommentId());
        log.info("댓글에 달린 좋아요 수 : {}",testComment.getCommentLikeList().size());
        return commentLikeDto;
    }

    @Override
    public List<PostDto> GetMyPosts(HttpServletRequest req) {
        List<PostDto> result = new ArrayList<>();
        String accessToken = extractToken(req);
        Member memberEntity = findMember(accessToken);
        List<Post> postList = memberEntity.getPostList();

        for (Post post : postList) {
            result.add(modelMapper.map(post, PostDto.class));
        }

        return result;
    }

    @Override
    public List<PostDto> GetPostLikeList(HttpServletRequest req) {
        List<PostDto> result = new ArrayList<>();
        String accessToken = extractToken(req);
        Member memberEntity = findMember(accessToken);

        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        for (PostLike postLike : postLikeList) {
            result.add(modelMapper.map(postLike.getPost(), PostDto.class));
        }

        return result;
    }

    private Member findMember(String accessToken) {
        String email = tokenProvider.extractUsername(accessToken);
        return memberRepository.findByEmail(email);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    private String extractToken(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtIllegalException("인증 토큰이 유효하지 않습니다.");
        }

        return authorizationHeader.substring(7);
    }
}
