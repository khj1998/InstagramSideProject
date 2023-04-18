package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockUserDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.entity.BlockedUser;
import CloneProject.InstagramClone.InstagramService.entity.Follow;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.exception.follow.BlockMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.UnBlockFailedException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.follow.UnfollowFailedException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.BlockedUserRepository;
import CloneProject.InstagramClone.InstagramService.repository.FollowRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final FollowRepository followRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public FollowDto addFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = findMemberByToken(accessToken); // 팔로우 거는 쪽
        Member toMember = memberRepository
                .findById(followDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("UserNameNotFoundException occurred")); // 팔로우 받는 쪽

        if (fromMember.getId().equals(toMember.getId())) {
            throw new FollowMySelfException("Cannot follow myself exception occurred");
        }

        Follow follow = Follow.builder()
                .following(fromMember)
                .follower(toMember)
                .build();

        followRepository.save(follow);
        FollowDto result = modelMapper.map(follow,FollowDto.class);
        result.setId(toMember.getId());
        return result;
    }

    @Override
    @Transactional
    public FollowDto unFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = findMemberByToken(accessToken);
        Member toMember = memberRepository
                .findById(followDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("UserNameNotFoundException occurred"));

        Follow follow = followRepository
                .findByFollowingIdAndFollowerId(fromMember.getId(), toMember.getId())
                .orElseThrow(() -> new UnfollowFailedException("Unfollow failed exception occurred"));

        followRepository.delete(follow);
        return modelMapper.map(follow,FollowDto.class);
    }

    @Override
    public BlockUserDto blockUser(BlockUserDto blockUserDto) {
        String accessToken = blockUserDto.getAccessToken();
        Member fromBannedMember = findMemberByToken(accessToken);
        Member toBannedMember = memberRepository
                .findById(blockUserDto.getBanId())
                .orElseThrow(() -> new UserNotFoundException("UserNotFoundException occurred"));

        if (fromBannedMember.getId().equals(toBannedMember.getId())) {
            throw new BlockMySelfException("BanMySelfException occurred");
        }

        BlockedUser blockedUser = BlockedUser.builder()
                .email(toBannedMember.getEmail())
                .fromBlockedUser(fromBannedMember)
                .toBlockedMember(toBannedMember)
                .build();
        blockedUserRepository.save(blockedUser);

        return modelMapper.map(blockedUser, BlockUserDto.class);
    }

    @Override
    public BlockUserDto unBlockUser(BlockUserDto blockUserDto) {
        String accessToken = blockUserDto.getAccessToken();
        Member fromBannedMember = findMemberByToken(accessToken);
        Member toBannedMember = memberRepository
                .findById(blockUserDto.getBanId())
                .orElseThrow(() -> new UserNotFoundException("UserNotFoundException occurred"));

        BlockedUser blockedUser = blockedUserRepository.findByFromBlockedIdAndToBlockedId(fromBannedMember.getId(), toBannedMember.getId())
                .orElseThrow(() -> new UnBlockFailedException("UnBlockedFailedException occurred"));
        blockedUserRepository.delete(blockedUser);

        return modelMapper.map(blockedUser, BlockUserDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowings(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenProvider.ExtractToken(req);
        Member memberEntity = findMemberByToken(accessToken);

        List<FollowDto> result = new ArrayList<>();
        List<Follow> followingList = memberEntity.getFollowingList();

        for (Follow follow : followingList) {
            result.add(modelMapper.map(follow.getFollower(),FollowDto.class));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowers(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenProvider.ExtractToken(req);
        Member memberEntity = findMemberByToken(accessToken);

        List<FollowDto> result = new ArrayList<>();
        List<Follow> followerList = memberEntity.getFollowerList();

        for (Follow follower : followerList) {
            result.add(modelMapper.map(follower.getFollowing(),FollowDto.class));
        }
        return result;
    }

    private Member findMemberByToken(String accessToken) {
        try {
            String email = tokenProvider.extractUsername(accessToken);
            return memberRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException occurred"));
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("AccessToken Expired");
        }
    }
}
