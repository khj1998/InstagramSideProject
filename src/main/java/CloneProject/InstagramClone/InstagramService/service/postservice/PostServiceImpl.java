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
        List<HashTag> sameTagChecker = new ArrayList<>();
        Member memberEntity = tokenService.FindMemberByToken(postDto.getAccessToken());
        Post postEntity = createPost(memberEntity,postDto);

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

    private Post createPost(Member memberEntity, PostDto postDto) {
        return Post.builder()
                .member(memberEntity)
                .title(postDto.getTitle())
                .content(postDto.getTitle())
                .imageUrl(postDto.getImageUrl())
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

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("get post Id : "+postId)
                .data(foundPostDto)
                .build();
    }

    /**
     * A function that converts and returns entities in a found post to Dto
     * @param foundPostEntity Post entity that found in database
     * @return PostDto Dto of the found post entity
     */
    protected PostDto getFoundPostDto(Post foundPostEntity) {
        return addHashTagToPostDto(foundPostEntity);
    }

    /**
     * A function to add hashtags to found post Dto
     * @param foundPostEntity Post entity that found in database
     * @return PostDto Post Dto with hashtag included
     */
    protected PostDto addHashTagToPostDto(Post foundPostEntity) {
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

        PostDto resDto = modelMapper.map(postEntity,PostDto.class);
        resDto.setHashTagList(postDto.getHashTagList());

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("edited post number : "+postDto.getId())
                .data(resDto)
                .build();
    }

    /**
     * A function that modifies data related to posts other than hashtags
     * @param postDto PostDto with data change information for post
     * @return Post Modified post Entity
     * @throws PostNotFoundException Occurs when a post is not found
     */
    protected Post updatePostWithoutHashTag(PostDto postDto) {
        Post postEntity = postRepository
                .findById(postDto.getId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        postEntity.ChangeTitle(postDto.getTitle());
        postEntity.ChangeContent(postDto.getContent());
        postEntity.ChangeImageUrl(postDto.getImageUrl());
        postRepository.save(postEntity);

        return postEntity;
    }

    /**
     * A function to find newly added hashtags to posts
     * @param postEntity Post entity before hashtag update
     * @param hashDtoList Hashtag Dto list of posts forwarded in request
     */
    protected void updateNewHashTag(Post postEntity,List<HashTagDto> hashDtoList) {
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

    /**
     * A function that stores hashtags in database that are not in existing posts
     * @param isNewHashTag Variable that determines hashtag that is not in the database
     * @param nowPostEntity Current Post Entity
     * @param hashTagName Representing hashtag's name
     */
    protected void createNewHashTag(boolean isNewHashTag,Post nowPostEntity,String hashTagName) {
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

    /**
     * A function that creates an intermediate entity that connects posts and hashtags.
     * @param newHashTag New hashtag that does not exist in DB
     * @param nowPostEntity Current post entity to which hashtag will be added
     */
    protected void saveHashTagMapping(HashTag newHashTag,Post nowPostEntity) {
        HashTagMapping newHashTagMapping = HashTagMapping.builder()
                .hashTag(newHashTag)
                .post(nowPostEntity)
                .build();
        hashTagMappingRepository.save(newHashTagMapping);
    }

    /**
     * A function to update hashtag removed from the post
     * @param nowPostEntity  Current Post Entity
     * @param hashTagDtoList A list of hashtags that came in requests to compare with existing hashtags in the post
     */
    protected void updateRemovedHashTag(Post nowPostEntity,List<HashTagDto> hashTagDtoList) {
        List<HashTag> nowHashTagList = getNowPostHashTagList(nowPostEntity);

        for (HashTag hashTag : nowHashTagList) {
            hashTag = compareHashTagAndHashTagDto(hashTag,hashTagDtoList);

            if (hashTag!=null) {
                removeHashTag(hashTag);
            }
        }
    }

    /**
     * A function that returns a list of hashtags for the current post
     * @param nowPostEntity Current Post Entity
     * @return A list of hashtags for the current post
     */
    protected List<HashTag> getNowPostHashTagList(Post nowPostEntity) {

        List<HashTag> nowHashTagList = new ArrayList<>();
        List<HashTagMapping> hashTagMappingList = nowPostEntity.getHashTagMappingList();

        for (HashTagMapping hashTagMappingEntity : hashTagMappingList) {
            nowHashTagList.add(hashTagMappingEntity.getHashTag());
        }

        return nowHashTagList;
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

    /**
     * A function that removes the hashtag.
     * Delete hashtags from DB that are not used outside the current post
     * @param hashTag hashtag entity to be removed
     */
    protected void removeHashTag(HashTag hashTag) {
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
        String deletedAt = getDeletedDate();

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete postId : "+postId)
                .updatedAt(deletedAt)
                .build();
    }

    /**
     * A function that returns the date the post was deleted
     * @return formatter Date format in the form yyyy-MM-dd HH:mm:ssz (deletion date)
     */
    protected String getDeletedDate() {
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

    /**
     * A function that works when user didn't add like to post.
     * Add like to post.
     * @param nowPostEntity Current Post Entity
     * @param memberEntity Member's entity to add like to post.
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when successfully add like to a post
     */
    protected ResponseEntity<ApiResponse> addLikeToPost(Post nowPostEntity,Member memberEntity) {
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

    /**
     * A function that cancels like if a post has already been registered like.
     * @param postLike PostLike entity has already been registered.
     * @return ResponseEntity<ApiResponse> ResponseEntity returned if successfully cancelled the likes in the post
     */
    protected ResponseEntity<ApiResponse> removeLikeToPost(PostLike postLike) {
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

        List<PostDto> postDtoList;
        List<Post> postList = memberEntity.getPostList();
        postDtoList = revertPostToPostDto(postList);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 작성한 게시물들 리스트")
                .data(postDtoList)
                .build();
    }

    /**
     * A function that converts a user's Post list into a PostDto list.
     * @param postList Post list posted by user.
     * @return List<PostDto> List of Posts that store the converted PostDto for response
     */
    protected List<PostDto> revertPostToPostDto(List<Post> postList) {
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

    /**
     * A function that converts hashtags to Dto and returns a HashTagDtoList.
     * @param hashTagMappingList HashTagMapping list that links posts to hashtags.
     * @return List<HashTagDto>
     */
    protected List<HashTagDto> getHashTagDto(List<HashTagMapping> hashTagMappingList) {
        HashTag hashTag;
        List<HashTagDto> HashTagDtoList = new ArrayList<>();

        for (HashTagMapping hashTagMapping : hashTagMappingList) {
            hashTag = hashTagMapping.getHashTag();
            HashTagDtoList.add(modelMapper.map(hashTag, HashTagDto.class));
        }
        return HashTagDtoList;
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

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 좋아요를 누른 글 리스트")
                .data(postDtoList)
                .build();
    }

    /**
     * A function that converts PostEntity associated with PostLike to Dto.
     * @param postLikeList List of PostLike entities to be converted to Dto
     * @return List<PostDto> PostDto list to be used for response body
     */
    protected List<PostDto> getPostDtoList(List<PostLike> postLikeList) {
        return postLikeList.stream()
                .map(postLike -> modelMapper.map(postLike.getPost(), PostDto.class))
                .collect(Collectors.toList());
    }
}
