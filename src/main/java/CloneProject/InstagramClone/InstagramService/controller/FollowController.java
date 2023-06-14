package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockMemberDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowResponse;
import CloneProject.InstagramClone.InstagramService.service.followservice.FollowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @ApiOperation("팔로잉 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "팔로잉 추가 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "팔로잉 추가 실패")
    })
    @PostMapping("/following")
    public ResponseEntity<FollowResponse> addFollowing(@RequestBody FollowDto followDto) {
        return followService.addFollow(followDto);
    }

    @ApiOperation("팔로잉 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "팔로잉 취소 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "팔로잉 취소 실패")
    })
    @DeleteMapping("/unfollowing")
    public ResponseEntity<FollowResponse> unFollowing(@RequestBody FollowDto followDto) {
        return followService.unFollow(followDto);
    }

    @ApiOperation("유저 차단")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "유저 차단 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "유저 차단 실패")
    })
    @PostMapping("/users/blocking")
    public ResponseEntity<FollowResponse> blockUser(@RequestBody BlockMemberDto blockMemberDto) {
        return followService.blockUser(blockMemberDto);
    }

    @ApiOperation("유저 차단 해제")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "유저 차단 해제"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "유저 차단 해제 실패")
    })
    @DeleteMapping("/users/unblocking")
    public ResponseEntity<RestApiResponse> unblockUser(@RequestBody BlockMemberDto blockMemberDto) {
        return followService.unBlockUser(blockMemberDto);
    }

    @ApiOperation("팔로잉 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "팔로잉 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "팔로잉 조회 실패")
    })
    @GetMapping("/followings/list")
    public ResponseEntity<RestApiResponse> getFollowings(HttpServletRequest req) {
        return followService.getFollowings(req);
    }

    @ApiOperation("팔로워 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "팔로워 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "팔로워 조회 실패")
    })
    @GetMapping("/followers/list")
    public ResponseEntity<RestApiResponse> getFollowers(HttpServletRequest req) {
        return followService.getFollowers(req);
    }

    @ApiOperation("차단한 유저 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "차단한 유저 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "차단한 유저 조회 실패")
    })
    @GetMapping("/users/blocked")
    public ResponseEntity<RestApiResponse> getBlockedUsers(HttpServletRequest req) {
        return followService.getBlockedUsers(req);
    }

    @ApiOperation("본인을 차단한 유저 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "본인을 차단한 유저 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "본인을 차단한 유저 조회 실패")
    })
    @GetMapping("/users/blocking")
    public ResponseEntity<RestApiResponse> getBlockingUsers(HttpServletRequest req) {
        return followService.getBlockingUsers(req);
    }
}
