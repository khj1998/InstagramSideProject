package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.entity.Comment;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.entity.PostLike;
import CloneProject.InstagramClone.InstagramService.exception.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.repository.CommentRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostLikeRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
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
public class PostServiceImpl implements PostService{

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
    public List<PostDto> getMyPosts(HttpServletRequest req) {
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
    public List<PostDto> getPostLikeList(HttpServletRequest req) {
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

    private String extractToken(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtIllegalException("인증 토큰이 유효하지 않습니다.");
        }

        return authorizationHeader.substring(7);
    }
}
