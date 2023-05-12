package CloneProject.InstagramClone.InstagramService.service.followservice;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockMemberDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface FollowService {
    ResponseEntity<FollowResponse> addFollow(FollowDto followDto);
    ResponseEntity<FollowResponse> unFollow(FollowDto followDto);
    ResponseEntity<ApiResponse> getFollowings(HttpServletRequest req);
    ResponseEntity<ApiResponse> getFollowers(HttpServletRequest req);
    ResponseEntity<FollowResponse> blockUser(BlockMemberDto blockMemberDto);
    ResponseEntity<ApiResponse> unBlockUser(BlockMemberDto blockMemberDto);
    ResponseEntity<ApiResponse> getBlockedUsers(HttpServletRequest req);
    ResponseEntity<ApiResponse> getBlockingUsers(HttpServletRequest req);
}
