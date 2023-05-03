package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTag;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTagMapping;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import CloneProject.InstagramClone.InstagramService.entity.post.PostLike;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagLimitException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNameNotValidException;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final HashTagRepository hashTagRepository;
    private final HashTagMappingRepository hashTagMappingRepository;

    /**
     * 같은 해시태그 중복 카운팅 방지
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddPost(PostDto postDto) throws JwtExpiredException {
        List<String> sameTagChecker = new ArrayList<>();
        Member memberEntity = tokenService.FindMemberByToken(postDto.getAccessToken());
        Post postEntity = Post.builder()
                .member(memberEntity)
                .title(postDto.getTitle())
                .content(postDto.getTitle())
                .imageUrl(postDto.getImageUrl())
                .build();

        if (postDto.getHashTagList().size() >= 30) {
            throw new HashTagLimitException("HashTagLimitException occurred");
        }

        for (HashTagDto hashTagDto : postDto.getHashTagList()) {

            if (!hashTagDto.getTagName().startsWith("#")) {
                throw new HashTagNameNotValidException("HashTagNameNotValidException occurred");
            }

            HashTag hashTag = hashTagRepository.findByTagName(hashTagDto.getTagName());
            HashTagMapping hashTagMapping;

            if (hashTag == null) {
                hashTag = HashTag.builder()
                        .tagName(hashTagDto.getTagName())
                        .tagCount(1L)
                        .build();
                hashTagMapping = HashTagMapping.builder()
                        .post(postEntity)
                        .hashTag(hashTag)
                        .build();
                hashTagMappingRepository.save(hashTagMapping);
            } else {
                hashTag.UpdateTagCount();
            }

            if (!sameTagChecker.contains(hashTag.getTagName())) {
                sameTagChecker.add(hashTag.getTagName());
                hashTagRepository.save(hashTag);
            }
        }

        postRepository.save(postEntity);
        PostDto resData = modelMapper.map(postEntity, PostDto.class);
        resData.setHashTagList(postDto.getHashTagList());

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Post")
                .data(resData)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> FindPost(HttpServletRequest req,Long postId) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        tokenService.isTokenValid(accessToken);
        Post postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));

        PostDto resData = modelMapper.map(postEntity,PostDto.class);
        List<HashTagMapping> hashTagMappingList = postEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMapping : hashTagMappingList) {
            HashTagDto hashTagDto = modelMapper.map(hashTagMapping.getHashTag(), HashTagDto.class);
            resData.getHashTagList().add(hashTagDto);
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("get post Id : "+postId)
                .data(resData)
                .build();
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> EditPost(PostDto postDto) {
        Post postEntity = postRepository
                .findById(postDto.getId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        postEntity.ChangeTitle(postDto.getTitle());
        postEntity.ChangeContent(postDto.getContent());
        postEntity.ChangeImageUrl(postDto.getImageUrl());

        postRepository.save(postEntity);
        PostDto resDto = modelMapper.map(postEntity,PostDto.class);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("edited post number : "+postDto.getId())
                .data(resDto)
                .build();
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> DeletePost(String postId) {
        Long id = Long.parseLong(postId);
        Post postEntity = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        List<HashTagMapping> hashTagMappingList = postEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMapping : hashTagMappingList) {
            hashTagRepository.delete(hashTagMapping.getHashTag());
            hashTagMappingRepository.delete(hashTagMapping);
        }
        postRepository.deleteById(id);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete postId : "+postId)
                .updatedAt(formatter.format(date))
                .build();
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
            PostLike newPostLike = PostLike.builder()
                    .member(memberEntity)
                    .post(postEntity)
                    .build();

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
    public ResponseEntity<ApiResponse> GetMyPosts(HttpServletRequest req) throws JwtExpiredException {
        List<PostDto> resDtoList = new ArrayList<>();
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Post> postList = memberEntity.getPostList();

        for (Post post : postList) {
            resDtoList.add(modelMapper.map(post, PostDto.class));
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 작성한 게시물들 리스트")
                .data(resDtoList)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetPostLikeList(HttpServletRequest req) throws JwtExpiredException {
        List<PostDto> resDtoList = new ArrayList<>();
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        for (PostLike postLike : postLikeList) {
            resDtoList.add(modelMapper.map(postLike.getPost(), PostDto.class));
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 좋아요를 누른 글 리스트")
                .data(resDtoList)
                .build();
    }
}
