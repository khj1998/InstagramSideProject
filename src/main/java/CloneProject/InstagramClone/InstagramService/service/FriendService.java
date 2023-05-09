package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FriendService {
    ResponseEntity<ApiResponse> AddFriend(List<FriendDto> friendDtoList);
    ResponseEntity<ApiResponse> DeleteFriend(List<FriendDto> friendDtoList);
    ResponseEntity<ApiResponse> GetFriendList(HttpServletRequest req);
}
