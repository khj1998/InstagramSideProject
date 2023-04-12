package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.entity.Follower;
import CloneProject.InstagramClone.InstagramService.entity.Following;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.exception.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.repository.FollowerRepository;
import CloneProject.InstagramClone.InstagramService.repository.FollowingRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public FollowDto addFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = findMemberByToken(accessToken); // 팔로우 거는 쪽
        Member toMember = memberRepository.findById(followDto.getId()).get(); // 팔로우 받는 쪽

        Following following = new Following();
        following.setFollowing(toMember);
        Follower follower = new Follower();
        follower.setFollower(fromMember);

        fromMember.getFollowingList().add(following);
        toMember.getFollowerList().add(follower);

        followingRepository.save(following);
        memberRepository.save(fromMember);
        followerRepository.save(follower);
        memberRepository.save(toMember);

        FollowDto result = modelMapper.map(following,FollowDto.class);
        result.setId(followDto.getId());
        return result;
    }

    @Override
    public FollowDto unFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = findMemberByToken(accessToken);
        Member toMember = memberRepository.findById(followDto.getId()).get();

        List<Following> followingList = fromMember.getFollowingList();
        List<Follower> followerList = toMember.getFollowerList();

        for (Following following : followingList) {
            if (following.getMember().getId().equals(following.getId())) {
                followingRepository.delete(following);
                break;
            }
        }

        for (Follower follower : followerList) {
            if (follower.getMember().getId().equals(fromMember.getId())) {
                followerRepository.delete(follower);
                break;
            }
        }
        return followDto;
    }

    @Override
    public List<FollowDto> getFollowingList(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenProvider.ExtractToken(req);
        Member memberEntity = findMemberByToken(accessToken);

        List<FollowDto> result = new ArrayList<>();
        List<Following> followingList = memberEntity.getFollowingList();

        for (Following following : followingList) {
            result.add(modelMapper.map(following,FollowDto.class));
        }
        return result;
    }

    @Override
    public List<FollowDto> getFollowerList(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenProvider.ExtractToken(req);
        Member memberEntity = findMemberByToken(accessToken);

        List<FollowDto> result = new ArrayList<>();
        List<Follower> followerList = memberEntity.getFollowerList();

        for (Follower follower : followerList) {
            result.add(modelMapper.map(follower,FollowDto.class));
        }
        return result;
    }

    private Member findMemberByToken(String accessToken) {
        try {
            String email = tokenProvider.extractUsername(accessToken);
            return memberRepository.findByEmail(email);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("AccessToken Expired");
        }
    }
}
