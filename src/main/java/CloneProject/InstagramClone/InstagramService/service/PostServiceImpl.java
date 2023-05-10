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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddPost(PostDto postDto) throws JwtExpiredException {
        List<HashTag> sameTagChecker = new ArrayList<>();
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

            if (hashTag == null) { // 게시글에 추가되지 않았고, DB 에도 해당 해시태그 정보가 없는 상태
                hashTag = HashTag.builder()
                        .tagName(hashTagDto.getTagName())
                        .tagCount(1L)
                        .build();
                hashTagMapping = HashTagMapping.builder()
                        .post(postEntity)
                        .hashTag(hashTag)
                        .build();
                sameTagChecker.add(hashTag);
            } else if (!sameTagChecker.contains(hashTag)) { // DB에 해시태그 정보가 있지만, 해당 게시글에는 추가되지 않은 상태
                sameTagChecker.add(hashTag);
                hashTag.AddTagCount();
                hashTagMapping = HashTagMapping.builder()
                        .post(postEntity)
                        .hashTag(hashTag)
                        .build();
            } else { // 이미 게시글에 추가된 해시태그이고, DB에도 있는 해시태그.
                continue;
            }
            hashTagMappingRepository.save(hashTagMapping);
        }
        PostDto resData = modelMapper.map(postEntity, PostDto.class);

        for (HashTag hashTag : sameTagChecker) {
            HashTagDto hashTagDto = HashTagDto.builder()
                    .tagName(hashTag.getTagName())
                    .build();
            resData.getHashTagList().add(hashTagDto);
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Post")
                .data(resData)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> FindPost(HttpServletRequest req, Long postId) {
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

        List<HashTagDto> hashDtoList = postDto.getHashTagList();
        checkNewHashTag(postEntity,hashDtoList);
        checkRemovedHashTag(postEntity,hashDtoList);

        PostDto resDto = modelMapper.map(postEntity,PostDto.class);
        resDto.setHashTagList(postDto.getHashTagList());

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("edited post number : "+postDto.getId())
                .data(resDto)
                .build();
    }

    private void checkNewHashTag(Post postEntity,List<HashTagDto> hashDtoList) {
        List<HashTag> nowHashTagList = getNowPostHashTagList(postEntity);
        boolean isNewHashTag;

        for (HashTagDto hashTagDto : hashDtoList) {
            HashTag hashTag = HashTag.builder()
                    .tagName(hashTagDto.getTagName())
                    .build();
            isNewHashTag = nowHashTagList.contains(hashTag);
            createNewHashTag(isNewHashTag,postEntity,hashTagDto.getTagName());
        }
    }

    private void createNewHashTag(boolean isNewHashTag,Post nowPostEntity,String hashTagName) {
        if (!isNewHashTag) return;

        HashTag findHashTag;
        HashTag newHashTag;
        HashTagMapping newHashTagMapping;
        findHashTag = hashTagRepository.findByTagName(hashTagName);

        if (findHashTag == null) {
            newHashTag = HashTag.builder()
                    .tagName(hashTagName)
                    .tagCount(1L)
                    .build();
            newHashTagMapping = HashTagMapping.builder()
                    .hashTag(newHashTag)
                    .post(nowPostEntity)
                    .build();
            hashTagMappingRepository.save(newHashTagMapping);
        }
    }

    private void checkRemovedHashTag(Post nowPostEntity,List<HashTagDto> hashTagDtoList) {
        List<HashTag> nowHashTagList = getNowPostHashTagList(nowPostEntity);

        for (HashTag hashTag : nowHashTagList) {
            hashTag = compareHashTagAndHashTagDto(hashTag,hashTagDtoList);

            if (hashTag!=null) {
                removeHashTag(hashTag);
            }
        }
    }

    private HashTag compareHashTagAndHashTagDto(HashTag hashTag,List<HashTagDto> hashTagDtoList) {
        String nowHashTagName;
        boolean isRemoved = true;

        for (HashTagDto hashTagDto : hashTagDtoList) {
            if (!hashTagDto.getTagName().startsWith("#")){
                throw new HashTagNameNotValidException("HashTagNameNotValidException occurred");
            }

            nowHashTagName = hashTag.getTagName();
            if (nowHashTagName.equals(hashTagDto.getTagName())) {
                isRemoved = false;
                break;
            }
        }

        return isRemoved ? hashTag:null;
    }

    private void removeHashTag(HashTag hashTag) {
        if (hashTag.getTagCount() >= 2) {
            hashTag.MinusTagCount();
            hashTagRepository.save(hashTag);
        }
        else {
            hashTagRepository.delete(hashTag);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> DeletePost(Long postId) {
        Post postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        postRepository.delete(postEntity);

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
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        HashTag hashTag;
        PostDto postDto;
        List<HashTagMapping> hashTagMappingList;
        List<PostDto> resDtoList = new ArrayList<>();
        List<Post> postList = memberEntity.getPostList();

        for (Post post : postList) {
            List<HashTagDto> HashTagDtoList = new ArrayList<>();
            hashTagMappingList = post.getHashTagMappingList();
            for (HashTagMapping hashTagMapping : hashTagMappingList) {
                hashTag = hashTagMapping.getHashTag();
                HashTagDtoList.add(modelMapper.map(hashTag, HashTagDto.class));
            }
            postDto = modelMapper.map(post, PostDto.class);
            postDto.setHashTagList(HashTagDtoList);
            resDtoList.add(postDto);
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

    private List<HashTag> getNowPostHashTagList(Post nowPostEntity) {

        List<HashTag> nowHashTagList = new ArrayList<>();
        List<HashTagMapping> hashTagMappingList = nowPostEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMappingEntity : hashTagMappingList) {
            nowHashTagList.add(hashTagMappingEntity.getHashTag());
        }

        return nowHashTagList;
    }
}
