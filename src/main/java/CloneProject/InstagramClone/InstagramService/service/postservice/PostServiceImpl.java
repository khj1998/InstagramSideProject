package CloneProject.InstagramClone.InstagramService.service.postservice;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostDto;
import CloneProject.InstagramClone.InstagramService.dto.post.PostLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
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
    public ResponseEntity<RestApiResponse> AddPost(PostDto postDto) {
        List<HashTag> hashTagList = new ArrayList<>();
        Member memberEntity = tokenService.FindMemberByToken(postDto.getAccessToken());
        Post postEntity = createPost(memberEntity,postDto);

        if (postDto.getHashTagList().size() >= 30) {
            throw new HashTagLimitException("HashTagLimitException occurred");
        }

        postDto.getHashTagList()
                .forEach(hashTagDto -> updateHashTag(hashTagList,postEntity,hashTagDto));
        PostDto responsePostDto = convertResponsePostDto(postEntity);
        addHashTagDtoToPostDto(responsePostDto,hashTagList);

        return createAddPostResponse(responsePostDto);
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

    private void createNewHashTagMapping(HashTag hashTag,Post postEntity) {
        HashTagMapping hashTagMapping = createHashTagMapping(hashTag,postEntity);
        hashTagMappingRepository.save(hashTagMapping);
    }

    private ResponseEntity<RestApiResponse> createAddPostResponse(PostDto responsePostDto) {
        return new RestApiResponse.ApiResponseBuilder<>()
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
    public ResponseEntity<RestApiResponse> FindPost(HttpServletRequest req, Long postId) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        tokenService.isTokenValid(accessToken);
        Post foundPostEntity = findPostById(postId);
        PostDto foundPostDto = getFoundPostDto(foundPostEntity);

        return createFindPostResponse(foundPostDto);
    }

    private ResponseEntity<RestApiResponse> createFindPostResponse(PostDto foundPostDto) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("get post Id : "+foundPostDto.getId())
                .data(foundPostDto)
                .build();
    }

    private PostDto getFoundPostDto(Post foundPostEntity) {
        PostDto foundPostDto = convertPostDto(foundPostEntity);
        foundPostDto.setHashTagList(getHashTagDtoList(foundPostEntity));
        return foundPostDto;
    }

    private List<HashTagDto> getHashTagDtoList(Post foundPostEntity) {
        List<HashTag> hashTagList = getHashTagListFromHashTagMapping(foundPostEntity.getHashTagMappingList());
        return hashTagList.stream()
                .map(this::convertHashTagDto)
                .collect(Collectors.toList());
    }

    private List<HashTag> getHashTagListFromHashTagMapping(List<HashTagMapping> hashTagMappingList) {
        return hashTagMappingList.stream()
                .map(HashTagMapping::getHashTag)
                .collect(Collectors.toList());
    }

    /**
     * A Function that modifies posts
     * @param postDto Dto of the post will be modified that came in as a request
     * @return ResponseEntity<ApiResponse> ResponseEntity returned upon successful post modification
     */
    @Override
    @Transactional
    public ResponseEntity<RestApiResponse> EditPost(PostDto postDto) {
        Post postEntity = updatePostWithoutHashTag(postDto);
        List<HashTagDto> hashDtoList = postDto.getHashTagList();
        updateNewHashTag(postEntity,hashDtoList);
        updateRemovedHashTag(postEntity,hashDtoList);

        PostDto resPostDto = modelMapper.map(postEntity,PostDto.class);
        resPostDto.setHashTagList(postDto.getHashTagList());

        return createEditPostResponse(resPostDto);
    }

    private ResponseEntity<RestApiResponse> createEditPostResponse(PostDto postDto) {
        return new RestApiResponse.ApiResponseBuilder<>()
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
        List<HashTag> newHashTagList = convertHashTagList(hashDtoList);

        newHashTagList.stream()
                .filter(newHashTag -> !nowHashTagList.contains(newHashTag))
                .forEach(newHashTag -> createNewHashTag(postEntity, newHashTag.getTagName()));
    }

    private void createNewHashTag(Post nowPostEntity,String hashTagName) {
        HashTag findHashTag = hashTagRepository.findByTagName(hashTagName);
        if (findHashTag == null) {
            HashTag newHashTag = createNewHashTag(hashTagName);
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
        List<HashTag> newHashTagList = convertHashTagList(hashTagDtoList);
        List<HashTag> deletedHashTagList = getDeletedHashTagList(nowHashTagList,newHashTagList);
        doHashTagRemoval(deletedHashTagList);
    }

    private List<HashTag> getNowPostHashTagList(Post nowPostEntity) {
        List<HashTagMapping> hashTagMappingList = nowPostEntity.getHashTagMappingList();
        return hashTagMappingList.stream()
                .map(HashTagMapping::getHashTag)
                .collect(Collectors.toList());
    }

    private List<HashTag> getDeletedHashTagList(List<HashTag> nowHashTagList,List<HashTag> newHashTagList) {
        return nowHashTagList.stream()
                .filter(nowHashTag -> !newHashTagList.contains(nowHashTag))
                .collect(Collectors.toList());
    }

    private void doHashTagRemoval(List<HashTag> deletedHashTagList) {
        for (HashTag hashTag : deletedHashTagList) {
            if (hashTag.getTagCount()<=1){
                removeHashTag(hashTag);
            }else {
                minusHashTagCount(hashTag);
            }
        }
    }

    private void minusHashTagCount(HashTag hashTag) {
        hashTag.MinusTagCount();
        hashTagRepository.save(hashTag);
    }

    private void removeHashTag(HashTag hashTag) {
        hashTagRepository.delete(hashTag);
    }

    /**
     * A function to delete a post
     * @param postId The id value of the post to be deleted
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when a post is deleted successfully
     * @throws PostNotFoundException Occurs when a post is not found
     */
    @Override
    @Transactional
    public ResponseEntity<RestApiResponse> DeletePost(Long postId) {
        Post postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        postRepository.delete(postEntity);

        return createDeletePostResponse(postId);
    }

    private ResponseEntity<RestApiResponse> createDeletePostResponse(Long postId) {
        String deletedAt = getDeletedDate();
        return new RestApiResponse.ApiResponseBuilder<>()
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
    public ResponseEntity<RestApiResponse> AddPostLike(PostLikeDto postLikeDto) {
        Member memberEntity = tokenService.FindMemberByToken(postLikeDto.getAccessToken());
        Post postEntity = findPostById(postLikeDto.getPostId());
        PostLike postLike = postLikeRepository.findByMemberIdAndPostId(memberEntity.getId(),postLikeDto.getPostId());

        return postLike==null ?
                addLikeToPost(postEntity,memberEntity) :
                removeLikeToPost(postLike);
    }

    private ResponseEntity<RestApiResponse> addLikeToPost(Post nowPostEntity, Member memberEntity) {
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

    private ResponseEntity<RestApiResponse> createAddPostLikeResponse(Post nowPostEntity, PostLike newPostLike) {
        PostLikeDto postLikeDto = convertPostLikeDto(newPostLike);
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message(nowPostEntity.getId() + "번 글에 좋아요를 등록하였습니다.")
                .data(postLikeDto)
                .build();
    }

    private ResponseEntity<RestApiResponse> removeLikeToPost(PostLike postLike) {
        PostLikeDto postLikeDto = convertPostLikeDto(postLike);
        postLikeRepository.delete(postLike);
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message(postLike.getPost().getId()+"번 글의 좋아요를 취소하였습니다.")
                .data(postLikeDto)
                .build();
    }

    /**
     * A function that looks up user's posts
     * @param req HttpServletRequest
     * @return ResponseEntity returned if successfully queried his or her post.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<RestApiResponse> GetMyPosts(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Post> postList = memberEntity.getPostList();
        List<PostDto> postDtoList = revertPostToPostDto(postList);

        return createGetMyPostsResponse(postDtoList);
    }

    private ResponseEntity<RestApiResponse> createGetMyPostsResponse(List<PostDto> postDtoList) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 작성한 게시물들 리스트")
                .data(postDtoList)
                .build();
    }

    private List<PostDto> revertPostToPostDto(List<Post> postList) {
        PostDto postDto;
        List<HashTagDto> hashTagDtoList;
        List<PostDto> postDtoList = new ArrayList<>();

        for (Post post : postList) {
            hashTagDtoList = getHashTagDto(post.getHashTagMappingList());
            postDto = convertPostDto(post);
            postDto.setHashTagList(hashTagDtoList);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    private List<HashTagDto> getHashTagDto(List<HashTagMapping> hashTagMappingList) {
        return hashTagMappingList.stream()
                .map(HashTagMapping::getHashTag)
                .map(this::convertHashTagDto)
                .collect(Collectors.toList());
    }

    /**
     * A function that looks up posts that users like.
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> ResponseEntity returned if the posts user posted were successfully viewed.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<RestApiResponse> GetPostLikeList(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<PostLike> postLikeList = memberEntity.getPostLikeList();
        List<PostDto> postDtoList = convertPostDtoList(postLikeList);

        return createGetPostLikeListResponse(postDtoList);
    }

    private ResponseEntity<RestApiResponse> createGetPostLikeListResponse(List<PostDto> postDtoList) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("내가 좋아요를 누른 글 리스트")
                .data(postDtoList)
                .build();
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
    }

    private List<PostDto> convertPostDtoList(List<PostLike> postLikeList) {
        return postLikeList.stream()
                .map(postLike -> modelMapper.map(postLike.getPost(), PostDto.class))
                .collect(Collectors.toList());
    }

    private List<HashTag> convertHashTagList(List<HashTagDto> hashDtoList) {
        return hashDtoList.stream()
                .map(hashTagDto -> HashTag.builder()
                        .tagName(hashTagDto.getTagName())
                        .build())
                .collect(Collectors.toList());
    }

    private PostDto convertResponsePostDto(Post postEntity) {
        return modelMapper.map(postEntity, PostDto.class);
    }

    private HashTagDto convertHashTagDto(HashTag hashTag) {
        return modelMapper.map(hashTag, HashTagDto.class);
    }

    private PostDto convertPostDto(Post postEntity) {
        return modelMapper.map(postEntity,PostDto.class);
    }

    private PostLikeDto convertPostLikeDto(PostLike postLike) {
        return modelMapper.map(postLike,PostLikeDto.class);
    }

    private Post createPost(Member memberEntity, PostDto postDto) {
        return Post.builder()
                .member(memberEntity)
                .title(postDto.getTitle())
                .content(postDto.getTitle())
                .imageUrl(postDto.getImageUrl())
                .build();
    }

    private HashTagMapping createHashTagMapping(HashTag hashTag,Post postEntity) {
        return HashTagMapping.builder()
                .hashTag(hashTag)
                .post(postEntity)
                .build();
    }

    private HashTag createNewHashTag(String tagName) {
        return HashTag.builder()
                .tagName(tagName)
                .tagCount(1L)
                .build();
    }
}
