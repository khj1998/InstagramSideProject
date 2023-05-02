package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.comment.Comment;
import CloneProject.InstagramClone.InstagramService.entity.comment.CommentLike;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import CloneProject.InstagramClone.InstagramService.exception.comment.CommentNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.CommentLikeRepository;
import CloneProject.InstagramClone.InstagramService.repository.CommentRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> EditComment(CommentDto commentDto) {
        Comment commentEntity = commentRepository
                .findById(commentDto.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("CommentNotFoundException occurred"));
        commentEntity.setContent(commentDto.getContent());
        commentRepository.save(commentEntity);
        CommentDto resDto =  modelMapper.map(commentEntity,CommentDto.class);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Comment Edit")
                .data(resDto)
                .build();
    }

    /**
     * 댓글 쓴 Member, 댓글 - Member, 글 - 댓글 연관관계 매핑
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddComment(CommentDto commentDto) throws JwtExpiredException,UsernameNotFoundException {
        Post postEntity = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        Member memberEntity = tokenService.FindMemberByToken(commentDto.getAccessToken());
        Comment commentEntity = modelMapper.map(commentDto, Comment.class);

        commentRepository.save(commentEntity);
        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        CommentDto resDto =  modelMapper.map(commentEntity, CommentDto.class);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Comment")
                .data(resDto)
                .build();
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddCommentLike(CommentLikeDto commentLikeDto) throws JwtExpiredException {
        Long commentId = commentLikeDto.getCommentId();
        Member memberEntity = tokenService.FindMemberByToken(commentLikeDto.getAccessToken());
        Comment commentEntity = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("CommentNotFoundException occurred"));
        CommentLike commentLike = new CommentLike();
        CommentLike commentLikeEntity = commentLikeRepository.findByMemberIdAndCommentId(memberEntity.getId(),commentEntity.getId());

        if (commentLikeEntity == null) {
            commentLike.setComment(commentEntity);
            commentLike.setMember(memberEntity);
            commentLikeRepository.save(commentLike);

            return new ApiResponse.ApiResponseBuilder<>()
                    .success(true)
                    .message("Id: "+commentEntity.getId()+" 댓글에 좋아요를 누르셨습니다")
                    .data(modelMapper.map(commentEntity, CommentDto.class))
                    .build();
        } else {
            commentLikeRepository.delete(commentLikeEntity);
            return new ApiResponse.ApiResponseBuilder<>()
                    .success(true)
                    .message("Id: "+commentEntity.getId()+" 댓글에 좋아요를 취소하셨습니다.")
                    .data(modelMapper.map(commentEntity, CommentDto.class))
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> DeleteComment(String commentId) {
        Long id = Long.parseLong(commentId);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        commentRepository.deleteById(id);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete CommentId : "+commentId)
                .updatedAt(formatter.format(date))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetMyComments(HttpServletRequest req) throws JwtExpiredException,UsernameNotFoundException {
        List<CommentDto> commentDtoList = new ArrayList<>();
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Comment> commentList = memberEntity.getCommentList();

        for (Comment comment : commentList) {
            commentDtoList.add(modelMapper.map(comment, CommentDto.class));
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get My Comments")
                .data(commentDtoList)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetMyCommentLikes(HttpServletRequest req) {
        List<CommentDto> resDto = new ArrayList<>();
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<CommentLike> commentLikeList = memberEntity.getCommentLikeList();

        for (CommentLike commentLike : commentLikeList) {
            resDto.add(modelMapper.map(commentLike.getComment(),CommentDto.class));
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("좋아요를 누른 댓글 리스트 조회")
                .data(resDto)
                .build();
    }
}
