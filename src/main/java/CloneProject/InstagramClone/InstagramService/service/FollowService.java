package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockUserDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FollowService {
    ResponseEntity<FollowResponse> addFollow(FollowDto followDto);
    void unFollow(FollowDto followDto);
    List<FollowDto> getFollowings(HttpServletRequest req);
    List<FollowDto> getFollowers(HttpServletRequest req);
    BlockUserDto blockUser(BlockUserDto blockUserDto);
    void unBlockUser(BlockUserDto blockUserDto);
    List<BlockUserDto> getBlockedUsers(HttpServletRequest req);
    List<BlockUserDto> getBlockingUsers(HttpServletRequest req);
}
