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

    @Override
    public PostDto FindPost(String postId) {
        Long id = Long.parseLong(postId);
        Post postEntity = postRepository.findById(id).get();
        return modelMapper.map(postEntity,PostDto.class);
    }

    @Override
    public PostDto EditPost(PostDto postDto) {
        Post postEntity = postRepository.findById(postDto.getId()).get();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setContent(postDto.getContent());
        postEntity.setImageUrl(postDto.getImageUrl());

        postRepository.save(postEntity);

        return modelMapper.map(postEntity,PostDto.class);
    }

    @Override
    public void DeletePost(String postId) {
        Long id = Long.parseLong(postId);
        postRepository.deleteById(id);
    }

    /**
     * 이미 좋아요를 추가한 상태라면 좋아요 취소
     */
    @Override
    public PostLikeDto AddPostLike(PostLikeDto postLikeDto) {
        Member memberEntity = findMember(postLikeDto.getAccessToken());
        Post postEntity = postRepository.findById(postLikeDto.getPostId()).get();
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

    private String extractToken(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtIllegalException("인증 토큰이 유효하지 않습니다.");
        }

        return authorizationHeader.substring(7);
    }
}
