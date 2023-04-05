package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.PostDto;
import CloneProject.InstagramClone.InstagramService.entity.Comment;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.repository.CommentRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final ModelMapper modelMapper;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public PostDto AddPost(PostDto postDto) {
        Post postEntity = modelMapper.map(postDto,Post.class);
        Member memberEntity = findMember(postDto.getAccessToken());
        postEntity.setMember(memberEntity);
        memberEntity.getPostList().add(postEntity);

        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        PostDto response = modelMapper.map(postEntity, PostDto.class);
        response.setAccessToken(postDto.getAccessToken());
        return response;
    }

    /**
     * 댓글 쓴 Member, 댓글 - Member, 글 - 댓글 연관관계 매핑
     */
    @Override
    public CommentDto AddComment(CommentDto commentDto) {
        Post postEntity = findPost(commentDto.getPostId());
        Member memberEntity = findMember(commentDto.getAccessToken());
        Comment commentEntity = modelMapper.map(commentDto, Comment.class);

        commentEntity.setPost(postEntity);
        commentEntity.setMember(memberEntity);
        postEntity.getCommentList().add(commentEntity);
        memberEntity.getCommentList().add(commentEntity);

        commentRepository.save(commentEntity);
        postRepository.save(postEntity);
        memberRepository.save(memberEntity);

        return modelMapper.map(commentEntity, CommentDto.class);
    }

    private Member findMember(String accessToken) {
        String email = tokenProvider.extractUsername(accessToken);
        return memberRepository.findByEmail(email);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }
}
