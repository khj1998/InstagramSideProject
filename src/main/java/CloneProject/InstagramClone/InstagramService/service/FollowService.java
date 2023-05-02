package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockUserDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FollowService {
    ResponseEntity<FollowResponse> addFollow(FollowDto followDto);
    ResponseEntity<FollowResponse> unFollow(FollowDto followDto);
    ResponseEntity<ApiResponse> getFollowings(HttpServletRequest req);
    ResponseEntity<ApiResponse> getFollowers(HttpServletRequest req);
    ResponseEntity<FollowResponse> blockUser(BlockUserDto blockUserDto);
    ResponseEntity<ApiResponse> unBlockUser(BlockUserDto blockUserDto);
    ResponseEntity<ApiResponse> getBlockedUsers(HttpServletRequest req);
    ResponseEntity<ApiResponse> getBlockingUsers(HttpServletRequest req);
}
