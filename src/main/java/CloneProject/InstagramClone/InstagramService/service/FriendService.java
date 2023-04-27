package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;

import java.util.List;

public interface FriendService {
    List<FriendDto> AddFriend(List<FriendDto> friendDtoList);
    List<FriendDto> DeleteFriend(List<FriendDto> friendDtoList);
}
