package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.*;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public PostDto AddPost(PostDto postDto) throws JwtExpiredException {
        Post postEntity = modelMapper.map(postDto,Post.class);
        Member memberEntity = tokenService.FindMemberByToken(postDto.getAccessToken());
        memberEntity.AddPost(postEntity);

        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        PostDto response = modelMapper.map(postEntity, PostDto.class);
        response.setAccessToken(postDto.getAccessToken());
        return response;
    }

    @Override
    @Transactional
    public PostDto FindPost(String postId) {
        Long id = Long.parseLong(postId);
        Post postEntity = postRepository
                .findById(id)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        return modelMapper.map(postEntity,PostDto.class);
    }

    @Override
    @Transactional
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
    @Transactional
    public void DeletePost(String postId) {
        Long id = Long.parseLong(postId);
        postRepository.deleteById(id);
    }

    /**
     * 이미 좋아요를 추가한 상태라면 좋아요 취소
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddPostLike(PostLikeDto postLikeDto) throws JwtExpiredException {
        ResponseEntity<ApiResponse> response;
        Member memberEntity = tokenService.FindMemberByToken(postLikeDto.getAccessToken());

        Post postEntity = postRepository
                .findById(postLikeDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));

        PostLike postLike = postLikeRepository.findByMemberIdAndPostId(memberEntity.getId(),postLikeDto.getPostId());

        if (postLike==null) {
            PostLike newPostLike = new PostLike();
            newPostLike.setMember(memberEntity);
            newPostLike.setPost(postEntity);
            postLikeDto.setPostTitle(postEntity.getTitle());
            postLikeRepository.save(newPostLike);
            response = new ApiResponse.ApiResponseBuilder<>()
                    .success(true)
                    .message(postEntity.getTitle()+"번 글에 좋아요를 등록하였습니다.")
                    .data(postLikeDto)
                    .build();
        } else {
            postLikeRepository.delete(postLike);
            response = new ApiResponse.ApiResponseBuilder<>()
                    .success(true)
                    .message(postEntity.getTitle()+"번 글에 좋아요를 취소하였습니다.")
                    .data(postLikeDto)
                    .build();
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> GetMyPosts(HttpServletRequest req) throws JwtExpiredException {
        List<PostDto> result = new ArrayList<>();
        String accessToken = tokenService.ExtractToken(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
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
        String accessToken = tokenService.ExtractToken(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        for (PostLike postLike : postLikeList) {
            result.add(modelMapper.map(postLike.getPost(), PostDto.class));
        }

        return result;
    }
}
