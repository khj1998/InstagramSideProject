package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FriendService {
    List<FriendDto> AddFriend(List<FriendDto> friendDtoList);
    void DeleteFriend(List<FriendDto> friendDtoList);
    List<FriendDto> GetFriendList(HttpServletRequest req);
}
