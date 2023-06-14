package CloneProject.InstagramClone.InstagramService.service.followservice;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockMemberDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface FollowService {
    ResponseEntity<FollowResponse> addFollow(FollowDto followDto);
    ResponseEntity<FollowResponse> unFollow(FollowDto followDto);
    ResponseEntity<RestApiResponse> getFollowings(HttpServletRequest req);
    ResponseEntity<RestApiResponse> getFollowers(HttpServletRequest req);
    ResponseEntity<FollowResponse> blockUser(BlockMemberDto blockMemberDto);
    ResponseEntity<RestApiResponse> unBlockUser(BlockMemberDto blockMemberDto);
    ResponseEntity<RestApiResponse> getBlockedUsers(HttpServletRequest req);
    ResponseEntity<RestApiResponse> getBlockingUsers(HttpServletRequest req);
}
