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

        Post foundPostEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        PostDto foundPostDto = getFoundPostDto(foundPostEntity);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("get post Id : "+postId)
                .data(foundPostDto)
                .build();
    }

    private PostDto getFoundPostDto(Post foundPostEntity) {
        PostDto foundPostDto = modelMapper.map(foundPostEntity,PostDto.class);
        addHashTagToPostDto(foundPostEntity,foundPostDto);
        return foundPostDto;
    }

    private void addHashTagToPostDto(Post foundPostEntity,PostDto foundPostDto) {
        List<HashTagMapping> hashTagMappingList = foundPostEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMapping : hashTagMappingList) {
            HashTagDto hashTagDto = modelMapper.map(hashTagMapping.getHashTag(), HashTagDto.class);
            foundPostDto.getHashTagList().add(hashTagDto);
        }
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
        findHashTag = hashTagRepository.findByTagName(hashTagName);

        if (findHashTag == null) {
            HashTag newHashTag = HashTag.builder()
                    .tagName(hashTagName)
                    .tagCount(1L)
                    .build();
            HashTagMapping newHashTagMapping = HashTagMapping.builder()
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

    private List<HashTag> getNowPostHashTagList(Post nowPostEntity) {

        List<HashTag> nowHashTagList = new ArrayList<>();
        List<HashTagMapping> hashTagMappingList = nowPostEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMappingEntity : hashTagMappingList) {
            nowHashTagList.add(hashTagMappingEntity.getHashTag());
        }

        return nowHashTagList;
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
        String deletedAt = getDeletedDate();

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete postId : "+postId)
                .updatedAt(deletedAt)
                .build();
    }

    private String getDeletedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddPostLike(PostLikeDto postLikeDto) throws JwtExpiredException {
        Member memberEntity = tokenService.FindMemberByToken(postLikeDto.getAccessToken());
        Post postEntity = postRepository
                .findById(postLikeDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        PostLike postLike = postLikeRepository.findByMemberIdAndPostId(memberEntity.getId(),postLikeDto.getPostId());

        return postLike==null ?
                addLikeToPost(postEntity,memberEntity) :
                removeLikeToPost(postLike);
    }

    private ResponseEntity<ApiResponse> addLikeToPost(Post nowPostEntity,Member memberEntity) {
        PostLike newPostLike = PostLike.builder()
                .member(memberEntity)
                .post(nowPostEntity)
                .build();
        postLikeRepository.save(newPostLike);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message(nowPostEntity.getId()+"번 글에 좋아요를 등록하였습니다.")
                .data(modelMapper.map(newPostLike, PostLikeDto.class))
                .build();
    }

    private ResponseEntity<ApiResponse> removeLikeToPost(PostLike postLike) {
        postLikeRepository.delete(postLike);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message(postLike.getPost().getId()+"번 글의 좋아요를 취소하였습니다.")
                .data(modelMapper.map(postLike, PostLikeDto.class))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetMyPosts(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<PostDto> postDtoList;
        List<Post> postList = memberEntity.getPostList();
        postDtoList = revertPostToPostDto(postList);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 작성한 게시물들 리스트")
                .data(postDtoList)
                .build();
    }

    private List<PostDto> revertPostToPostDto(List<Post> postList) {
        PostDto postDto;
        List<HashTagMapping> hashTagMappingList;
        List<HashTagDto> hashTagDtoList;
        List<PostDto> postDtoList = new ArrayList<>();

        for (Post post : postList) {
            hashTagMappingList = post.getHashTagMappingList();
            hashTagDtoList = getHashTagDto(hashTagMappingList);
            postDto = modelMapper.map(post,PostDto.class);
            postDto.setHashTagList(hashTagDtoList);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    private List<HashTagDto> getHashTagDto(List<HashTagMapping> hashTagMappingList) {
        HashTag hashTag;
        List<HashTagDto> HashTagDtoList = new ArrayList<>();

        for (HashTagMapping hashTagMapping : hashTagMappingList) {
            hashTag = hashTagMapping.getHashTag();
            HashTagDtoList.add(modelMapper.map(hashTag, HashTagDto.class));
        }
        return HashTagDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetPostLikeList(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        List<PostDto> postLikeDtoList = getPostDtoList(postLikeList);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 좋아요를 누른 글 리스트")
                .data(postLikeDtoList)
                .build();
    }

    private List<PostDto> getPostDtoList(List<PostLike> postLikeList) {
        List<PostDto> postLikeDtoList = new ArrayList<>();
        PostDto postDto;

        for (PostLike postLike : postLikeList) {
            postDto = modelMapper.map(postLike.getPost(), PostDto.class);
            postLikeDtoList.add(postDto);
        }

        return postLikeDtoList;
    }
}
