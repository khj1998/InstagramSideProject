package CloneProject.InstagramClone.InstagramService.service.friendservice;

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
import CloneProject.InstagramClone.InstagramService.service.TokenService;
import CloneProject.InstagramClone.InstagramService.service.friendservice.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FriendServiceImpl class for side project.
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
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when adding a friend is successful
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
        tokenService.isTokenValid(accessToken);
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        List<Friend> addFriendList = getAddFriendList(friendDtoList,fromMember);
        friendRepository.saveAll(addFriendList);
        List<FriendDto> addFriendDtoList = getAddFriendDtoList(addFriendList);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Friends")
                .data(addFriendDtoList)
                .build();
    }

    /**
     * A function that returns the list of friends to add
     * @param friendDtoList Dto list of friends to add
     * @param fromMember Current member entity to add a friend to add a friend
     * @return List<Friend> Friend entity list to add to friend storage
     * @throws UserIdNotFoundException Cannot find user id in database
     */
    protected List<Friend> getAddFriendList(List<FriendDto> friendDtoList,Member fromMember) {
        List<Friend> addFriendList = new ArrayList<>();

        for (FriendDto friendDto : friendDtoList) {
            Member toMember = memberRepository.findById(friendDto.getId())
                    .orElseThrow(() -> new UserIdNotFoundException("UserNotFoundException occurred"));
            Friend friend = createFriendEntity(fromMember,toMember);
            addFriendList.add(friend);
        }
        return addFriendList;
    }

    /**
     * A function that creates friend entity
     * @param fromMember Current member entity to add a friend to add a friend
     * @param toMember Friend entity to be added to friend storage
     * @return Friend A friend entity
     */
    protected Friend createFriendEntity(Member fromMember, Member toMember) {
        return Friend.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build();
    }

    /**
     * A Function that generate friendDtoList to be included in the response body
     * @param addFriendList List of friend entities to be converted to Dto
     * @return List<FriendDto> friendDtoList to be included in the response body
     */
    protected List<FriendDto> getAddFriendDtoList(List<Friend> addFriendList) {
        return addFriendList.stream()
                .map(friend -> modelMapper.map(friend,FriendDto.class))
                .collect(Collectors.toList());
    }

    /**
     * A function that deletes friend
     * @param friendDtoList List of friends dto you want to delete
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when friend deletion is successful
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
        List<Friend> deleteFriendList = findDeletedFriend(fromMember,friendDtoList);
        friendRepository.deleteAll(deleteFriendList);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete Friends")
                .build();
    }

    /**
     * A function to delete registered friends
     * @param fromMember Subject entity deleting friends
     * @param friendDtoList A function that converts the requested friend Dto list to be deleted into a friend entity list
     * @return List<Friend> Friend entity list to be deleted
     * @throws FriendNoFoundException Unregistered friend.
     */
    protected List<Friend> findDeletedFriend(Member fromMember, List<FriendDto> friendDtoList) {
        List<Friend> deleteFriendList = new ArrayList<>();

        for (FriendDto friendDto : friendDtoList) {
            Long toMemberId = friendDto.getId();
            Friend friend = friendRepository.findByFromMemberIdAndToMemberId(fromMember.getId(),toMemberId)
                    .orElseThrow(()->new FriendNoFoundException("FriendNoFoundException occurred"));
            deleteFriendList.add(friend);
        }
        return deleteFriendList;
    }

    /**
     * A function to get a list of registered friends
     * @param req HttpServletRequest
     * @return ResponseEntity<ApiResponse> ResponseEntity returned when a friend list lookup is successful
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

    /**
     * A function that generates a friendDtoList to enter the response body
     * @param friendList List of friend entities to be converted to Dto
     * @return List<FriendDto>
     */
    protected List<FriendDto> getFriendDtoList(List<Friend> friendList) {
        return friendList.stream()
                .map(friend -> modelMapper.map(friend.getToMember(),FriendDto.class))
                .collect(Collectors.toList());
    }
}
