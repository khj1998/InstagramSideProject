package CloneProject.InstagramClone.InstagramService.service.postservice;

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
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.*;
import CloneProject.InstagramClone.InstagramService.service.TokenService;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * PostServiceImpl class for side project.
 * This class is in charge of post function
 * @author Quokka_khj
 */
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
     * A function that adds post
     * @param postDto Posts to be added as requested Dto
     * @return ResponseEntity<ApiResponse> ResponseEntity returned upon successful post addition
     * @throws HashTagLimitException Occurs when the hashtag limit is exceeded
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddPost(PostDto postDto) {
        List<HashTag> hashTagList = new ArrayList<>();
        Member memberEntity = tokenService.FindMemberByToken(postDto.getAccessToken());
        Post postEntity = createPost(memberEntity,postDto);

        if (postDto.getHashTagList().size() >= 30) {
            throw new HashTagLimitException("HashTagLimitException occurred");
        }

        postDto.getHashTagList()
                .forEach(hashTagDto -> updateHashTag(hashTagList,postEntity,hashTagDto));
        PostDto responsePostDto = createResponsePostDto(postEntity);
        addHashTagDtoToPostDto(responsePostDto,hashTagList);

        return createAddPostResponse(responsePostDto);
    }

    private PostDto createResponsePostDto(Post postEntity) {
        return modelMapper.map(postEntity, PostDto.class);
    }

    private void addHashTagDtoToPostDto(PostDto responsePostDto,List<HashTag> hashTagList) {
        for (HashTag hashTag : hashTagList) {
            HashTagDto hashTagDto = HashTagDto.builder()
                    .tagName(hashTag.getTagName())
                    .build();
            responsePostDto.getHashTagList().add(hashTagDto);
        }
    }

    private void updateHashTag(List<HashTag> hashTagList,Post postEntity,HashTagDto hashTagDto) {
        if (!hashTagDto.getTagName().startsWith("#")) {
            throw new HashTagNameNotValidException("HashTagNameNotValidException occurred");
        }
        HashTag hashTag = hashTagRepository.findByTagName(hashTagDto.getTagName());

        if (hashTag == null) {
            hashTag = createNewHashTag(hashTagDto.getTagName());
        } else if (!hashTagList.contains(hashTag)) {
            hashTag.AddTagCount();
        } else {
            return;
        }
        hashTagList.add(hashTag);
        createNewHashTagMapping(hashTag,postEntity);
    }

    private HashTag createNewHashTag(String tagName) {
        return HashTag.builder()
                .tagName(tagName)
                .tagCount(1L)
                .build();
    }

    private void createNewHashTagMapping(HashTag hashTag,Post postEntity) {
        HashTagMapping hashTagMapping = HashTagMapping.builder()
                .post(postEntity)
                .hashTag(hashTag)
                .build();
        hashTagMappingRepository.save(hashTagMapping);
    }

    private Post createPost(Member memberEntity, PostDto postDto) {
        return Post.builder()
                .member(memberEntity)
                .title(postDto.getTitle())
                .content(postDto.getTitle())
                .imageUrl(postDto.getImageUrl())
                .build();
    }

    private ResponseEntity<ApiResponse> createAddPostResponse(PostDto responsePostDto) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Post")
                .data(responsePostDto)
                .build();
    }

    /**
     * A function to find posts by request-on-id value
     * @see #getFoundPostDto(Post foundPostEntity)
     * @param req HttpServletRequest
     * @param postId Unique id of the post to find
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when a post is successfully found
     * @throws PostNotFoundException Occurs when a post is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> FindPost(HttpServletRequest req, Long postId) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        tokenService.isTokenValid(accessToken);

        Post foundPostEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        PostDto foundPostDto = getFoundPostDto(foundPostEntity);

        return createFindPostResponse(foundPostDto);
    }

    private ResponseEntity<ApiResponse> createFindPostResponse(PostDto foundPostDto) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("get post Id : "+foundPostDto.getId())
                .data(foundPostDto)
                .build();
    }

    private PostDto getFoundPostDto(Post foundPostEntity) {
        return addHashTagToPostDto(foundPostEntity);
    }

    private PostDto addHashTagToPostDto(Post foundPostEntity) {
        PostDto foundPostDto = modelMapper.map(foundPostEntity,PostDto.class);
        List<HashTagMapping> hashTagMappingList = foundPostEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMapping : hashTagMappingList) {
            HashTagDto hashTagDto = modelMapper.map(hashTagMapping.getHashTag(), HashTagDto.class);
            foundPostDto.getHashTagList().add(hashTagDto);
        }

        return foundPostDto;
    }

    /**
     * A Function that modifies posts
     * @param postDto Dto of the post will be modified that came in as a request
     * @return ResponseEntity<ApiResponse> ResponseEntity returned upon successful post modification
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> EditPost(PostDto postDto) {
        Post postEntity = updatePostWithoutHashTag(postDto);
        List<HashTagDto> hashDtoList = postDto.getHashTagList();
        updateNewHashTag(postEntity,hashDtoList);
        updateRemovedHashTag(postEntity,hashDtoList);

        PostDto resPostDto = modelMapper.map(postEntity,PostDto.class);
        resPostDto.setHashTagList(postDto.getHashTagList());

        return createEditPostResponse(resPostDto);
    }

    private ResponseEntity<ApiResponse> createEditPostResponse(PostDto postDto) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("edited post number : "+postDto.getId())
                .data(postDto)
                .build();
    }

    private Post updatePostWithoutHashTag(PostDto postDto) {
        Post postEntity = postRepository
                .findById(postDto.getId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        postEntity.ChangeTitle(postDto.getTitle());
        postEntity.ChangeContent(postDto.getContent());
        postEntity.ChangeImageUrl(postDto.getImageUrl());
        postRepository.save(postEntity);

        return postEntity;
    }

    private void updateNewHashTag(Post postEntity,List<HashTagDto> hashDtoList) {
        List<HashTag> nowHashTagList = getNowPostHashTagList(postEntity);
        boolean isNewHashTag;

        List<HashTag> updateHashTagList = getHashTagList(hashDtoList);

        for (HashTagDto hashTagDto : hashDtoList) {
            HashTag hashTag = HashTag.builder()
                    .tagName(hashTagDto.getTagName())
                    .build();
            isNewHashTag = nowHashTagList.contains(hashTag);
            createNewHashTag(isNewHashTag,postEntity,hashTagDto.getTagName());
        }
    }

    private List<HashTag> getHashTagList(List<HashTagDto> hashDtoList) {
        return hashDtoList.stream()
                .map(hashTagDto -> HashTag.builder()
                        .tagName(hashTagDto.getTagName())
                        .build())
                .collect(Collectors.toList());
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
            saveHashTagMapping(newHashTag,nowPostEntity);
        }
    }

    private void saveHashTagMapping(HashTag newHashTag,Post nowPostEntity) {
        HashTagMapping newHashTagMapping = HashTagMapping.builder()
                .hashTag(newHashTag)
                .post(nowPostEntity)
                .build();
        hashTagMappingRepository.save(newHashTagMapping);
    }

    private void updateRemovedHashTag(Post nowPostEntity,List<HashTagDto> hashTagDtoList) {
        List<HashTag> nowHashTagList = getNowPostHashTagList(nowPostEntity);

        for (HashTag hashTag : nowHashTagList) {
            hashTag = compareHashTagAndHashTagDto(hashTag,hashTagDtoList);

            if (hashTag!=null) {
                removeHashTag(hashTag);
            }
        }
    }

    private List<HashTag> getNowPostHashTagList(Post nowPostEntity) {
        List<HashTagMapping> hashTagMappingList = nowPostEntity.getHashTagMappingList();
        return hashTagMappingList.stream()
                .map(HashTagMapping::getHashTag)
                .collect(Collectors.toList());
    }

    /**
     * A function that compares the hashtag of the current post with the hashtag delivered in the request to find the removed hashtag.
     * @param hashTag Hashtag entity to be determined if removed
     * @param hashTagDtoList List of hashtags Dto that came in as a request
     * @return Removed hashtag entity, null if not removed
     * @throws HashTagNameNotValidException Occurs when hashtag name is not valid
     */
    protected HashTag compareHashTagAndHashTagDto(HashTag hashTag,List<HashTagDto> hashTagDtoList) {
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

    /**
     * A function to delete a post
     * @param postId The id value of the post to be deleted
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when a post is deleted successfully
     * @throws PostNotFoundException Occurs when a post is not found
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> DeletePost(Long postId) {
        Post postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        postRepository.delete(postEntity);

        return createDeletePostResponse(postId);
    }

    private ResponseEntity<ApiResponse> createDeletePostResponse(Long postId) {
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

    /**
     * A function to add likes to a post
     * @param postLikeDto Dto which used for adding like to a post
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when likes are successfully added to a post.
     * @throws PostNotFoundException Occurs when a post is not found
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddPostLike(PostLikeDto postLikeDto) {
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
        PostLike newPostLike = createNewPostLike(nowPostEntity,memberEntity);
        postLikeRepository.save(newPostLike);

        return createAddPostLikeResponse(nowPostEntity,newPostLike);
    }

    private PostLike createNewPostLike(Post nowPostEntity,Member memberEntity) {
        return PostLike.builder()
                .member(memberEntity)
                .post(nowPostEntity)
                .build();
    }

    private ResponseEntity<ApiResponse> createAddPostLikeResponse(Post nowPostEntity,PostLike newPostLike) {
        PostLikeDto postLikeDto = modelMapper.map(newPostLike, PostLikeDto.class);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message(nowPostEntity.getId() + "번 글에 좋아요를 등록하였습니다.")
                .data(postLikeDto)
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

    /**
     * A function that looks up user's posts
     * @param req HttpServletRequest
     * @return ResponseEntity returned if successfully queried his or her post.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetMyPosts(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Post> postList = memberEntity.getPostList();
        List<PostDto> postDtoList = revertPostToPostDto(postList);

        return createGetMyPostsResponse(postDtoList);
    }

    private ResponseEntity<ApiResponse> createGetMyPostsResponse(List<PostDto> postDtoList) {
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
            postDto = modelMapper.map(post, PostDto.class);
            postDto.setHashTagList(hashTagDtoList);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    private List<HashTagDto> getHashTagDto(List<HashTagMapping> hashTagMappingList) {
        return hashTagMappingList.stream()
                .map(HashTagMapping::getHashTag)
                .map(hashTag -> modelMapper.map(hashTag, HashTagDto.class))
                .collect(Collectors.toList());
    }

    /**
     * A function that looks up posts that users like.
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> ResponseEntity returned if the posts user posted were successfully viewed.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetPostLikeList(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        List<PostDto> postDtoList = getPostDtoList(postLikeList);

        return createGetPostLikeListResponse(postDtoList);
    }

    private ResponseEntity<ApiResponse> createGetPostLikeListResponse(List<PostDto> postDtoList) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 좋아요를 누른 글 리스트")
                .data(postDtoList)
                .build();
    }

    private List<PostDto> getPostDtoList(List<PostLike> postLikeList) {
        return postLikeList.stream()
                .map(postLike -> modelMapper.map(postLike.getPost(), PostDto.class))
                .collect(Collectors.toList());
    }
}
