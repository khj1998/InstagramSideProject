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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    public FollowDto addFollow(FollowDto followDto) {
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

    private Member findMemberByToken(String accessToken) {
        try {
            String email = tokenProvider.extractUsername(accessToken);
            return memberRepository.findByEmail(email);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("AccessToken Expired");
        }
    }
}
