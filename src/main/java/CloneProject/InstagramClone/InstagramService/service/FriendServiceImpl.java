package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import CloneProject.InstagramClone.InstagramService.entity.friend.Friend;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.exception.friend.DuplicatedFriendException;
import CloneProject.InstagramClone.InstagramService.exception.friend.FriendMinSelectException;
import CloneProject.InstagramClone.InstagramService.exception.friend.FriendNoFoundException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.FriendRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<FriendDto> AddFriend(List<FriendDto> friendDtoList) {
        if (friendDtoList.size() == 0) {
            throw new FriendMinSelectException("FriendMinSelectException occurred");
        }

        String accessToken = friendDtoList.get(0).getAccessToken();
        Member fromMember = tokenService.FindMemberByToken(accessToken);
        Long fromMemberId = fromMember.getId();
        List<FriendDto> resDtoList = new ArrayList<>();

        for (FriendDto friendDto : friendDtoList) {
            Member toMember = memberRepository.findById(friendDto.getId())
                    .orElseThrow(() -> new UserNotFoundException("UserNotFoundException occurred"));
            Long toMemberId = toMember.getId();
            Friend friend = new Friend();
            friend.setFromMember(fromMember);
            friend.setToMember(toMember);

            Optional<Friend> myFriend = friendRepository.findByFromMemberIdAndToMemberId(fromMemberId,toMemberId);
            myFriend.ifPresent(m -> {
                throw new DuplicatedFriendException("DuplicatedFriendException occurred");
            });

            friendRepository.save(friend);
            FriendDto f = modelMapper.map(friend.getToMember(),FriendDto.class);
            f.setCreatedAt(friend.getCreatedAt());
            resDtoList.add(f);
        }

        return resDtoList;
    }

    @Override
    @Transactional
    public void DeleteFriend(List<FriendDto> friendDtoList) {
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
    }

    @Override
    public List<FriendDto> GetFriendList(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        Member memberEntity = tokenService.FindMemberByToken(accessToken);
        List<Friend> friendList = memberEntity.getFriendList();
        List<FriendDto> friendDtoList = new ArrayList<>();

        for (Friend friend : friendList) {
            FriendDto friendDto = modelMapper.map(friend.getToMember(),FriendDto.class);
            friendDto.setCreatedAt(friend.getCreatedAt());
            friendDtoList.add(friendDto);
        }

        return friendDtoList;
    }
}
