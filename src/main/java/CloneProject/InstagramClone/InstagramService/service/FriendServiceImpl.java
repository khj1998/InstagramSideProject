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

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

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

    @Override
    public ResponseEntity<ApiResponse> GetFriendList(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Friend> friendList = memberEntity.getFriendList();
        List<FriendDto> responseDtoList = new ArrayList<>();

        for (Friend friend : friendList) {
            FriendDto friendDto = modelMapper.map(friend.getToMember(),FriendDto.class);
            friendDto.setCreatedAt(friend.getCreatedAt());
            responseDtoList.add(friendDto);
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get Friends")
                .data(responseDtoList)
                .build();
    }
}
