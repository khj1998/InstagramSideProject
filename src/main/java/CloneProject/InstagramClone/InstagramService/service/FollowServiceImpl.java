package CloneProject.InstagramClone.InstagramService.service;

import static CloneProject.InstagramClone.InstagramService.config.SpringConst.*;
import CloneProject.InstagramClone.InstagramService.dto.follow.BlockUserDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import CloneProject.InstagramClone.InstagramService.entity.member.BlockedMember;
import CloneProject.InstagramClone.InstagramService.entity.follow.Follow;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.exception.follow.*;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.BlockedMemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.FollowRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final BlockedMemberRepository blockedMemberRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ResponseEntity<FollowResponse> addFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken); // 팔로우 거는 쪽
        Member toMember = memberRepository
                .findById(followDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("UserNameNotFoundException occurred")); // 팔로우 받는 쪽

        if (fromMember.getId().equals(toMember.getId())) {
            throw new FollowMySelfException("Cannot follow myself exception occurred");
        }

        if (fromMember.getFollowingList().size() >= FOLLOW_LIMIT_NUMBER) {
            throw new FollowLimitException("FollowLimitException occurred");
        }

        Follow follow = Follow.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build();
        followRepository.save(follow);

        FollowDto toMemberDto = modelMapper.map(toMember,FollowDto.class);
        toMemberDto.setId(toMember.getId());

        FollowDto fromMemberDto = modelMapper.map(fromMember,FollowDto.class);
        fromMemberDto.setId(fromMember.getId());

        return new FollowResponse.FollowResponseBuilder<>()
                .success(true)
                .message("Add Following")
                .fromMember(fromMemberDto)
                .toMember(toMemberDto)
                .build();
    }

    @Override
    @Transactional
    public void unFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Member toMember = memberRepository
                .findById(followDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("UserNameNotFoundException occurred"));

        Follow follow = followRepository
                .findByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())
                .orElseThrow(() -> new UnfollowFailedException("Unfollow failed exception occurred"));

        followRepository.delete(follow);
        FollowDto result = modelMapper.map(follow,FollowDto.class);
        result.setId(followDto.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowings(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<FollowDto> result = new ArrayList<>();
        List<Follow> followingList = memberEntity.getFollowingList();

        for (Follow follow : followingList) {
            result.add(modelMapper.map(follow.getToMember(),FollowDto.class));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowers(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<FollowDto> result = new ArrayList<>();
        List<Follow> followerList = memberEntity.getFollowerList();

        for (Follow follower : followerList) {
            result.add(modelMapper.map(follower.getFromMember(),FollowDto.class));
        }
        return result;
    }

    @Override
    @Transactional
    public BlockUserDto blockUser(BlockUserDto blockUserDto) {
        String accessToken = blockUserDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken); // 차단 거는쪽
        Member toMember = memberRepository
                .findById(blockUserDto.getId())
                .orElseThrow(() -> new UserNotFoundException("UserNotFoundException occurred")); //차단 대상

        if (fromMember.getId().equals(toMember.getId())) {
            throw new BlockMySelfException("BanMySelfException occurred");
        }

        BlockedMember blockedUser = BlockedMember.builder()
                .email(toMember.getEmail())
                .blockingMember(fromMember)
                .blockedMember(toMember)
                .build();

        blockedMemberRepository.save(blockedUser);
        BlockUserDto result = modelMapper.map(blockedUser, BlockUserDto.class);
        result.setId(blockUserDto.getId());

        return result;
    }

    @Override
    @Transactional
    public void unBlockUser(BlockUserDto blockUserDto) {
        String accessToken = blockUserDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Member toMember = memberRepository
                .findById(blockUserDto.getId())
                .orElseThrow(() -> new UserNotFoundException("UserNotFoundException occurred"));

        BlockedMember blockedUser = blockedMemberRepository
                .findByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())
                .orElseThrow(() -> new UnBlockFailedException("UnBlockedFailedException occurred"));
        blockedMemberRepository.delete(blockedUser);

        BlockUserDto result = modelMapper.map(blockedUser, BlockUserDto.class);
        result.setId(blockedUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockUserDto> getBlockedUsers(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<BlockUserDto> result = new ArrayList<>();
        List<BlockedMember> blockedMemberList = memberEntity.getBlockingList();

        for (BlockedMember blockedMember : blockedMemberList) {
            result.add(modelMapper.map(blockedMember.getToMember(),BlockUserDto.class));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockUserDto> getBlockingUsers(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);

        List<BlockUserDto> result = new ArrayList<>();
        List<BlockedMember> blockedMemberList = memberEntity.getBlockedList();

        for (BlockedMember blockedMember : blockedMemberList) {
            result.add(modelMapper.map(blockedMember.getFromMember(), BlockUserDto.class));
        }

        return result;
    }
}
