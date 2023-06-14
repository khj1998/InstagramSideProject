package CloneProject.InstagramClone.InstagramService.service.commentservice;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.comment.Comment;
import CloneProject.InstagramClone.InstagramService.entity.comment.CommentLike;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import CloneProject.InstagramClone.InstagramService.exception.comment.CommentNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.CommentLikeRepository;
import CloneProject.InstagramClone.InstagramService.repository.CommentRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import CloneProject.InstagramClone.InstagramService.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    /**
     * A function to add comment to a post
     * @param commentDto Comment information object as a Request
     * @return ResponseEntitu<ApiResponse> A response that is returned when adding comment is successful
     */
    @Override
    @Transactional
    public ResponseEntity<RestApiResponse> AddComment(CommentDto commentDto) {
        Post postEntity = findPostById(commentDto.getPostId());
        Member memberEntity = tokenService.FindMemberByToken(commentDto.getAccessToken());
        Comment commentEntity = createCommentEntity(postEntity,memberEntity,commentDto.getContent());
        commentRepository.save(commentEntity);
        CommentDto responseCommentDto =  convertCommentDto(commentEntity);

        return createAddCommentResponse(responseCommentDto);
    }

    private Comment createCommentEntity(Post postEntity,Member memberEntity,String content) {
        return Comment.builder()
                .post(postEntity)
                .member(memberEntity)
                .content(content)
                .build();
    }

    private ResponseEntity<RestApiResponse> createAddCommentResponse(CommentDto commentDto) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Comment")
                .data(commentDto)
                .build();
    }

    /**
     * A function that acts as editing comment
     * @param commentDto Comment information object as a Request
     * @return ResponseEntity<ApiResponse> A response that is returned when editing comment is successful
     */
    @Override
    @Transactional
    public ResponseEntity<RestApiResponse> EditComment(CommentDto commentDto) {
        Comment commentEntity = findCommentById(commentDto.getCommentId());
        commentEntity.ChangeContent(commentDto.getContent());
        commentRepository.save(commentEntity);

        return createEditCommentResponse(commentEntity);
    }

    private ResponseEntity<RestApiResponse> createEditCommentResponse(Comment commentEntity) {
        CommentDto commentDto = modelMapper.map(commentEntity,CommentDto.class);
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Comment Edit")
                .data(commentDto)
                .build();
    }

    /**
     * A function to add likes to comment
     * @param commentLikeDto Comment like information object as a Request
     * @return ResponseEntity<ApiResponse> A response that is returned when adding comment like is successful
     */
    @Override
    @Transactional
    public ResponseEntity<RestApiResponse> AddCommentLike(CommentLikeDto commentLikeDto) {
        Long commentId = commentLikeDto.getCommentId();
        Member memberEntity = tokenService.FindMemberByToken(commentLikeDto.getAccessToken());
        Comment commentEntity = findCommentById(commentId);
        CommentLike findCommentLikeEntity = commentLikeRepository.findByMemberIdAndCommentId(memberEntity.getId(),commentEntity.getId());
        CommentLike newCommentLike = findCommentLikeEntity==null ?
                addCommentLike(commentEntity,memberEntity) : deleteCommentLike(findCommentLikeEntity);

        return newCommentLike==null ?
                createAddCommentLikeResponse(commentEntity) : createDeleteCommentLikeResponse(commentEntity);
    }

    private CommentLike addCommentLike(Comment commentEntity,Member memberEntity) {
        CommentLike commentLike = createCommentLike(commentEntity,memberEntity);
        commentLikeRepository.save(commentLike);
        return commentLike;
    }

    private CommentLike deleteCommentLike(CommentLike commentLike) {
        commentLikeRepository.delete(commentLike);
        return null;
    }

    private ResponseEntity<RestApiResponse> createAddCommentLikeResponse(Comment commentEntity) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Id: "+commentEntity.getId()+" 댓글에 좋아요를 누르셨습니다")
                .data(modelMapper.map(commentEntity, CommentDto.class))
                .build();
    }

    private ResponseEntity<RestApiResponse> createDeleteCommentLikeResponse(Comment commentEntity) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Id: "+commentEntity.getId()+" 댓글에 좋아요를 취소하셨습니다.")
                .build();
    }

    /**
     * A function to delete comment
     * @param commentId comment Id value as a Request
     * @return ResponseEntity<ApiResponse> A response that is returned when deleting comment is successful
     */
    @Override
    @Transactional
    public ResponseEntity<RestApiResponse> DeleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        return createDeleteCommentResponse(commentId);
    }

    private ResponseEntity<RestApiResponse> createDeleteCommentResponse(Long commentId) {
        String deleteDate = createDeleteDate();
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete CommentId : "+commentId)
                .updatedAt(deleteDate)
                .build();
    }

    private String createDeleteDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    /**
     * A function that looks up comments created by users themselves
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> The response that is returned when the comment lookup you created is successful
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<RestApiResponse> GetMyComments(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Comment> commentList = memberEntity.getCommentList();
        List<CommentDto> commentDtoList = getCommentDtoList(commentList);

        return createGetMyCommentsResponse(commentDtoList);
    }

    private ResponseEntity<RestApiResponse> createGetMyCommentsResponse(List<CommentDto> commentDtoList) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get My Comments")
                .data(commentDtoList)
                .build();
    }

    private List<CommentDto> getCommentDtoList(List<Comment> commentList) {
        return commentList.stream()
                .map(this::convertCommentDto)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<RestApiResponse> GetMyCommentLikes(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<CommentLike> commentLikeList = memberEntity.getCommentLikeList();
        List<CommentDto> commentDtoList = createCommentDtoList(commentLikeList);

        return createGetMyCommentLikeResponse(commentDtoList);
    }

    private List<CommentDto> createCommentDtoList(List<CommentLike> commentLikeList) {
        return commentLikeList.stream()
                .map(commentLike -> modelMapper.map(commentLike.getComment(),CommentDto.class))
                .collect(Collectors.toList());
    }

    private ResponseEntity<RestApiResponse> createGetMyCommentLikeResponse(List<CommentDto> commentDtoList) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("좋아요를 누른 댓글 리스트 조회")
                .data(commentDtoList)
                .build();
    }

    private CommentDto convertCommentDto(Comment commentEntity) {
        return modelMapper.map(commentEntity, CommentDto.class);
    }

    private Post findPostById(Long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("CommentNotFoundException occurred"));
    }

    private CommentLike createCommentLike(Comment commentEntity,Member memberEntity) {
        return CommentLike.builder()
                .comment(commentEntity)
                .member(memberEntity)
                .build();
    }

}
