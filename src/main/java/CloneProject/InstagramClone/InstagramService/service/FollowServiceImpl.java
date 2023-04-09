package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowerDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowingDto;
import CloneProject.InstagramClone.InstagramService.entity.Follow;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.exception.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.repository.FollowRepository;
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
    private final FollowRepository followRepository;
    private final ModelMapper modelMapper;

    @Override
    public FollowerDto addFollower(FollowerDto followerDto) {
        return null;
    }

    @Override
    public FollowerDto addFollowing(FollowingDto followingDto) {
        String accessToken = followingDto.getAccessToken();
        Member fromMember = findMemberByToken(accessToken);
        Member toMember = memberRepository.findById(followingDto.getFollowingId()).get();

        Follow followingEntity = modelMapper.map(followingDto,Follow.class);
        followingEntity.setFollowing(toMember);

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
