package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.friend.Friend;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.exception.friend.DuplicatedFriendException;
import CloneProject.InstagramClone.InstagramService.exception.friend.FriendMinSelectException;
import CloneProject.InstagramClone.InstagramService.exception.friend.FriendNoFoundException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserIdNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.FriendRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * FriendServiceImpl Class for side project.
 * This class is in charge of friend function.
 * @author Quokka_khj
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    /**
     * A function that adds friend
     * @param friendDtoList List of newly added friend account information.
     * @return ResponseEntity<ApiResponse> Returns the friendDtoList as body.
     * @throws FriendMinSelectException At least one friend information must be selected.
     * @throws UserIdNotFoundException Cannot find user id in database
     * @throws DuplicatedFriendException Cannot re-register an already registered friend.
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> AddFriend(List<FriendDto> friendDtoList) {
        if (friendDtoList.size() == 0) {
            throw new FriendMinSelectException("FriendMinSelectException occurred");
        }

        String accessToken = friendDtoList.get(0).getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Long fromMemberId = fromMember.getId();
        List<FriendDto> responseDtoList = new ArrayList<>();

        for (FriendDto friendDto : friendDtoList) {
            Member toMember = memberRepository.findById(friendDto.getId())
                    .orElseThrow(() -> new UserIdNotFoundException("UserNotFoundException occurred"));
            Long toMemberId = toMember.getId();

            Friend friend = Friend.builder()
                    .fromMember(fromMember)
                    .toMember(toMember)
                    .build();

            Optional<Friend> myFriend = friendRepository.findByFromMemberIdAndToMemberId(fromMemberId,toMemberId);
            myFriend.ifPresent(m -> {
                throw new DuplicatedFriendException("DuplicatedFriendException occurred");
            });

            friendRepository.save(friend);
            FriendDto f = modelMapper.map(friend.getToMember(),FriendDto.class);
            f.setCreatedAt(friend.getCreatedAt());
            responseDtoList.add(f);
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Friends")
                .data(responseDtoList)
                .build();
    }

    /**
     * A function that deletes friend
     * @param friendDtoList List of friends dto you want to delete
     * @return ResponseEntity<ApiResponse> Returns the friend delete response
     * @throws FriendMinSelectException At least one friend information must be selected.
     * @throws FriendNoFoundException Unregistered friend.
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> DeleteFriend(List<FriendDto> friendDtoList) {
        if (friendDtoList.size() == 0) {
            throw new FriendMinSelectException("FriendMinSelectException occurred");
        }

        String accessToken = friendDtoList.get(0).getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Long fromMemberId = fromMember.getId();

        for (FriendDto friendDto : friendDtoList) {
            Long toMemberId = friendDto.getId();
            Friend friend = friendRepository.findByFromMemberIdAndToMemberId(fromMemberId,toMemberId)
                    .orElseThrow(()->new FriendNoFoundException("FriendNoFoundException occurred"));
            friendRepository.delete(friend);
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete Friends")
                .build();
    }

    /**
     * A function to get a list of registered friends
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse>
     */
    @Override
    public ResponseEntity<ApiResponse> GetFriendList(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Friend> friendList = memberEntity.getFriendList();
        List<FriendDto> friendDtoList = getFriendDtoList(friendList);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get Friends")
                .data(friendDtoList)
                .build();
    }

    private List<FriendDto> getFriendDtoList(List<Friend> friendList) {
        List<FriendDto> friendDtoList = new ArrayList<>();

        for (Friend friend : friendList) {
            FriendDto friendDto = modelMapper.map(friend.getToMember(),FriendDto.class);
            friendDto.setCreatedAt(friend.getCreatedAt());
            friendDtoList.add(friendDto);
        }
        return friendDtoList;
    }
}
