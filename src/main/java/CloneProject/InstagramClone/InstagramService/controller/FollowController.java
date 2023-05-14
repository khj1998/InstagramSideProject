package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockMemberDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import CloneProject.InstagramClone.InstagramService.service.followservice.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/following")
    public ResponseEntity<FollowResponse> addFollowing(@RequestBody FollowDto followDto) {
        return followService.addFollow(followDto);
    }

    @DeleteMapping("/unfollowing")
    public ResponseEntity<FollowResponse> unFollowing(@RequestBody FollowDto followDto) {
        return followService.unFollow(followDto);
    }

    @PostMapping("/users/blocking")
    public ResponseEntity<FollowResponse> blockUser(@RequestBody BlockMemberDto blockMemberDto) {
        return followService.blockUser(blockMemberDto);
    }

    @DeleteMapping("/users/unblocking")
    public ResponseEntity<ApiResponse> unblockUser(@RequestBody BlockMemberDto blockMemberDto) {
        return followService.unBlockUser(blockMemberDto);
    }

    @GetMapping("/followings/list")
    public ResponseEntity<ApiResponse> getFollowings(HttpServletRequest req) {
        return followService.getFollowings(req);
    }

    @GetMapping("/followers/list")
    public ResponseEntity<ApiResponse> getFollowers(HttpServletRequest req) {
        return followService.getFollowers(req);
    }

    @GetMapping("/users/blocked")
    public ResponseEntity<ApiResponse> getBlockedUsers(HttpServletRequest req) {
        return followService.getBlockedUsers(req);
    }

    @GetMapping("/users/blocking")
    public ResponseEntity<ApiResponse> getBlockingUsers(HttpServletRequest req) {
        return followService.getBlockingUsers(req);
    }
}
