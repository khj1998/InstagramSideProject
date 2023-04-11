package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.entity.Follower;
import CloneProject.InstagramClone.InstagramService.entity.Following;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.exception.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.repository.FollowerRepository;
import CloneProject.InstagramClone.InstagramService.repository.FollowingRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

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
        followerRepository.save(follower);
        memberRepository.save(fromMember);
        memberRepository.save(toMember);

        return modelMapper.map(following,FollowDto.class);
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
    public List<FollowDto> getFollowingList(HttpServletRequest req) {
        String accessToken = tokenProvider.ExtractToken(req);
        return null;
    }

    @Override
    public List<FollowDto> getFollowerList(HttpServletRequest req) {
        String accessToken = tokenProvider.ExtractToken(req);
        return null;
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
