package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.post.CommentLikeDto;
import CloneProject.InstagramClone.InstagramService.entity.Comment;
import CloneProject.InstagramClone.InstagramService.entity.CommentLike;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.exception.comment.CommentNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.CommentLikeRepository;
import CloneProject.InstagramClone.InstagramService.repository.CommentRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public CommentDto EditComment(CommentDto commentDto) {
        Comment commentEntity = commentRepository
                .findById(commentDto.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("CommentNotFoundException occurred"));
        commentEntity.setContent(commentDto.getContent());
        commentRepository.save(commentEntity);

        return modelMapper.map(commentEntity,CommentDto.class);
    }

    /**
     * 댓글 쓴 Member, 댓글 - Member, 글 - 댓글 연관관계 매핑
     */
    @Override
    public CommentDto AddComment(CommentDto commentDto) throws JwtExpiredException,UsernameNotFoundException {
        Post postEntity = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("PostNotFoundException occurred"));
        Member memberEntity = findMemberByToken(commentDto.getAccessToken());
        Comment commentEntity = modelMapper.map(commentDto, Comment.class);

        memberEntity.AddComment(commentEntity);
        postEntity.AddComment(commentEntity);

        commentRepository.save(commentEntity);
        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        return modelMapper.map(commentEntity, CommentDto.class);
    }

    @Override
    public CommentLikeDto AddCommentLike(CommentLikeDto commentLikeDto) throws JwtExpiredException {
        Long commentId = commentLikeDto.getCommentId();
        CommentLike commentLike = new CommentLike();
        Comment commentEntity = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("CommentNotFoundException occurred"));;

        commentEntity.AddCommentLike(commentLike);

        commentLikeRepository.save(commentLike);
        commentRepository.save(commentEntity);
        return commentLikeDto;
    }

    @Override
    public void DeleteComment(String commentId) {
        Long id = Long.parseLong(commentId);
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> GetMyComments(HttpServletRequest req) throws JwtExpiredException,UsernameNotFoundException {
        List<CommentDto> result = new ArrayList<>();
        String accessToken = extractToken(req);
        Member memberEntity = findMemberByToken(accessToken);
        List<Comment> commentList = memberEntity.getCommentList();

        for (Comment comment : commentList) {
            result.add(modelMapper.map(comment, CommentDto.class));
        }

        return result;
    }

    private Member findMemberByToken(String accessToken) {
        try {
            String email = tokenProvider.extractUsername(accessToken);
            return memberRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("UserNameNotFoundException occurred"));
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("AccessToken Expired");
        }
    }

    private String extractToken(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtIllegalException("인증 토큰이 유효하지 않습니다.");
        }

        return authorizationHeader.substring(7);
    }
}
