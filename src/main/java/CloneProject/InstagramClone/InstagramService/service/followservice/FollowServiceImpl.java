package CloneProject.InstagramClone.InstagramService.service.followservice;

import static CloneProject.InstagramClone.InstagramService.config.SpringConst.*;
import CloneProject.InstagramClone.InstagramService.dto.follow.BlockMemberDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import CloneProject.InstagramClone.InstagramService.entity.member.BlockedMember;
import CloneProject.InstagramClone.InstagramService.entity.follow.Follow;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.exception.follow.*;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserIdNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.BlockedMemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.FollowRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.service.TokenService;
import CloneProject.InstagramClone.InstagramService.service.followservice.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FollowServiceImpl Class for side project.
 * This class is in charge of user follow function and user block function.
 * @author Quokka_khj
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final BlockedMemberRepository blockedMemberRepository;
    private final ModelMapper modelMapper;

    /**
     * A function that follow users
     * @param followDto Objects that send follow-related data
     * @return ResponseEntity<FollowResponse> Returns the follow response format as body.
     * @throws FollowMySelfException Cannot follow my account
     * @throws FollowLimitException Account's following limit exceeded
     */
    @Override
    @Transactional
    public ResponseEntity<FollowResponse> addFollow(FollowDto followDto) throws JwtExpiredException {
        String accessToken = followDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Member toMember = memberRepository
                .findById(followDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("UserNameNotFoundException occurred"));

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

    /**
     * A function that unfollow users
     * @param followDto Objects that send follow-related data
     * @return ResponseEntity<FollowResponse> Returns the follow response format as body.
     * @throws UnfollowFailedException Unfollowing process failed
     */
    @Override
    @Transactional
    public ResponseEntity<FollowResponse> unFollow(FollowDto followDto) throws JwtExpiredException {
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

        return new FollowResponse.FollowResponseBuilder<>()
                .success(true)
                .message("Un Following Id : "+followDto.getId())
                .build();
    }

    /**
     * A function that looks up a follow list in an account
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> Returns the following dtol list as body.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getFollowings(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<FollowDto> followDtoList = getFollowDtoList(memberEntity.getFollowingList());

        return createGetFollowingsResponse(followDtoList);
    }

    private List<FollowDto> getFollowDtoList(List<Follow> followingList) {
        return followingList.stream()
                .map(follow -> modelMapper.map(follow.getToMember(),FollowDto.class))
                .collect(Collectors.toList());
    }

    private ResponseEntity<ApiResponse> createGetFollowingsResponse(List<FollowDto> followDtoList) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get User Following List")
                .data(followDtoList)
                .build();
    }

    /**
     * A function to look up a list of followers in an account
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> Returns the followers dto list as body.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getFollowers(HttpServletRequest req) throws JwtExpiredException {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<FollowDto> followerDtoList = getFollowerDtoList(memberEntity.getFollowerList());

        return createGetFollowerResponse(followerDtoList);
    }

    private List<FollowDto> getFollowerDtoList(List<Follow> followerList) {
        return followerList.stream()
                .map(follower -> modelMapper.map(follower.getFromMember(),FollowDto.class))
                .collect(Collectors.toList());
    }

    private ResponseEntity<ApiResponse> createGetFollowerResponse(List<FollowDto> followerDtoList) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get User Follower List")
                .data(followerDtoList)
                .build();
    }

    /**
     * A function that block users
     * @param blockMemberDto Object that sends blocked user information
     * @return ResponseEntity<ApiResponse> Returns the follow response format as body.
     * @throws UserIdNotFoundException Cannot find user id in database
     * @throws BlockMySelfException Blocking my account is invalid
     */
    @Override
    @Transactional
    public ResponseEntity<FollowResponse> blockUser(BlockMemberDto blockMemberDto) {
        String accessToken = blockMemberDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Member toMember = findToMemberById(blockMemberDto.getId());

        BlockedMember blockedUser = createBlockedMember(fromMember,toMember);
        blockedMemberRepository.save(blockedUser);

        return createFollowResponse(fromMember,toMember);
    }

    private Member findToMemberById(Long id) {
        return memberRepository
                .findById(id)
                .orElseThrow(() -> new UserIdNotFoundException("UserNotFoundException occurred"));
    }

    private ResponseEntity<FollowResponse> createFollowResponse(Member fromMember,Member toMember) {
        BlockMemberDto fromMemberDto = modelMapper.map(fromMember, BlockMemberDto.class);
        BlockMemberDto toMemberDto = modelMapper.map(toMember, BlockMemberDto.class);

        return new FollowResponse.FollowResponseBuilder<>()
                .success(true)
                .message("blocking member")
                .fromMember(fromMemberDto)
                .toMember(toMemberDto)
                .build();
    }

    private BlockedMember createBlockedMember(Member fromMember, Member toMember) {
        return BlockedMember.builder()
                .email(toMember.getEmail())
                .blockingMember(fromMember)
                .blockedMember(toMember)
                .build();
    }

    /**
     * A function to turn off blocked users
     * @param blockMemberDto Object that sends blocked user information
     * @return ResponseEntity<ApiResponse> Returns unblocking success response.
     * @throws UserIdNotFoundException Cannot find user id in database
     * @throws UnBlockFailedException Unblocking process failed
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> unBlockUser(BlockMemberDto blockMemberDto) {
        String accessToken = blockMemberDto.getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Member toMember = findToMemberById(blockMemberDto.getId());

        BlockedMember blockedUser = findBlockedMember(fromMember,toMember);
        blockedMemberRepository.delete(blockedUser);

        return createUnBlockUserResponse(blockMemberDto);
    }

    private BlockedMember findBlockedMember(Member fromMember,Member toMember) {
        return blockedMemberRepository
                .findByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())
                .orElseThrow(() -> new UnBlockFailedException("UnBlockedFailedException occurred"));
    }

    private ResponseEntity<ApiResponse> createUnBlockUserResponse(BlockMemberDto blockMemberDto) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("unblock user Id : " + blockMemberDto.getId())
                .build();
    }

    /**
     * A function to query blocked users in my account
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> Returns the blocking members list as body.
     * @throws UsernameNotFoundException Cannot find username in database
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getBlockedUsers(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<BlockMemberDto> responseDtoList = getBlockedMemberDtoByToMember(memberEntity.getBlockingList());

        return createGetBlockedUsersResponse(responseDtoList);
    }

    private List<BlockMemberDto> getBlockedMemberDtoByToMember(List<BlockedMember> blockedMemberList) {
        return blockedMemberList.stream()
                .map(blockedMember -> modelMapper.map(blockedMember.getToMember(),BlockMemberDto.class))
                .collect(Collectors.toList());
    }

    private ResponseEntity<ApiResponse> createGetBlockedUsersResponse(List<BlockMemberDto> responseDtoList) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("get all blocked users")
                .data(responseDtoList)
                .build();
    }

    /**
     * A function that looks up the user who blocked me
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> Returns the blocked members list as body.
     * @throws UsernameNotFoundException Cannot find username in database
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getBlockingUsers(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<BlockMemberDto> responseDtoList = getBlockedMemberDtoByFromMember(memberEntity.getBlockedList());

        return createGetBlockingUsersResponse(responseDtoList);
    }

    private List<BlockMemberDto> getBlockedMemberDtoByFromMember(List<BlockedMember> blockedMemberList) {
        return blockedMemberList.stream()
                .map(blockedMember -> modelMapper.map(blockedMember.getFromMember(), BlockMemberDto.class))
                .collect(Collectors.toList());
    }

    private ResponseEntity<ApiResponse> createGetBlockingUsersResponse(List<BlockMemberDto> responseDtoList) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("users who blocking my account")
                .data(responseDtoList)
                .build();
    }
}
