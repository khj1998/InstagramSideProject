package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.entity.*;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.*;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ModelMapper modelMapper;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    public PostDto AddPost(PostDto postDto) throws JwtExpiredException {
        Post postEntity = modelMapper.map(postDto,Post.class);
        Member memberEntity = findMemberByToken(postDto.getAccessToken());
        memberEntity.AddPost(postEntity);

        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        PostDto response = modelMapper.map(postEntity, PostDto.class);
        response.setAccessToken(postDto.getAccessToken());
        return response;
    }

    @Override
    public PostDto FindPost(String postId) {
        Long id = Long.parseLong(postId);
        Post postEntity = postRepository
                .findById(id)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        return modelMapper.map(postEntity,PostDto.class);
    }

    @Override
    public PostDto EditPost(PostDto postDto) {
        Post postEntity = postRepository
                .findById(postDto.getId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
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
    public PostLikeDto AddPostLike(PostLikeDto postLikeDto) throws JwtExpiredException {
        Member memberEntity = findMemberByToken(postLikeDto.getAccessToken());
        Post postEntity = postRepository
                .findById(postLikeDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));

        PostLike postLikeEntity = new PostLike();
        memberEntity.AddPostLike(postLikeEntity);
        postEntity.AddPostLike(postLikeEntity);

        postLikeRepository.save(postLikeEntity);
        postRepository.save(postEntity);
        memberRepository.save(memberEntity);
        postLikeDto.setPostTitle(postEntity.getTitle());
        return postLikeDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> GetMyPosts(HttpServletRequest req) throws JwtExpiredException {
        List<PostDto> result = new ArrayList<>();
        String accessToken = tokenProvider.ExtractToken(req);
        Member memberEntity = findMemberByToken(accessToken);
        List<Post> postList = memberEntity.getPostList();

        for (Post post : postList) {
            result.add(modelMapper.map(post, PostDto.class));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> GetPostLikeList(HttpServletRequest req) throws JwtExpiredException {
        List<PostDto> result = new ArrayList<>();
        String accessToken = tokenProvider.ExtractToken(req);
        Member memberEntity = findMemberByToken(accessToken);

        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        for (PostLike postLike : postLikeList) {
            result.add(modelMapper.map(postLike.getPost(), PostDto.class));
        }

        return result;
    }

    private Member findMemberByToken(String accessToken) {
        try {
            String email = tokenProvider.extractUsername(accessToken);
            return memberRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException occurred"));
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("AccessToken Expired");
        }
    }
}
