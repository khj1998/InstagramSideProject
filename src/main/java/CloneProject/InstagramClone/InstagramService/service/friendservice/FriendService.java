package CloneProject.InstagramClone.InstagramService.service.friendservice;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FriendService {
    ResponseEntity<RestApiResponse> AddFriend(List<FriendDto> friendDtoList);
    ResponseEntity<RestApiResponse> DeleteFriend(List<FriendDto> friendDtoList);
    ResponseEntity<RestApiResponse> GetFriendList(HttpServletRequest req);
}
