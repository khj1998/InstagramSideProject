package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.PostDto;
import CloneProject.InstagramClone.InstagramService.entity.Comment;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public PostDto AddPost(PostDto postDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

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
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Post postEntity = findPost(commentDto.getPostId());
        Member member = findMember(commentDto.getAccessToken());
        Comment commentEntity = modelMapper.map(commentDto, Comment.class);

        postEntity.getCommentList().add(commentEntity);


        return null;
    }

    private Member findMember(String accessToken) {
        String email = tokenProvider.extractUsername(accessToken);
        return memberRepository.findByEmail(email);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }
}
