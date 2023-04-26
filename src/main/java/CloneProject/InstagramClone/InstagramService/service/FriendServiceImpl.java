package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.FriendDto;
import CloneProject.InstagramClone.InstagramService.repository.FollowRepository;
import CloneProject.InstagramClone.InstagramService.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final FollowRepository followRepository;
    private final FriendRepository friendRepository;


    @Override
    public FriendDto AddFriend(FriendDto friendDto) {
        return null;
    }
}
