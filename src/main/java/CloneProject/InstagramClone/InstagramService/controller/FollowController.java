package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockUserDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/following")
    public ResponseEntity<ApiResponse> addFollowing(@RequestBody FollowDto followDto) {
        FollowDto resDto = followService.addFollow(followDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Following Id : "+ resDto.getId())
                .data(resDto)
                .build();
    }

    @PostMapping("/unfollowing")
    public ResponseEntity<ApiResponse> unFollowing(@RequestBody FollowDto followDto) {
        FollowDto resDto = followService.unFollow(followDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Un Following Id : "+resDto.getId())
                .data(resDto)
                .build();
    }

    @PostMapping("/users/blocking")
    public ResponseEntity<ApiResponse> blockUser(@RequestBody BlockUserDto blockUserDto) {
        BlockUserDto resDto = followService.blockUser(blockUserDto);
        return  new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("blocked user email : "+resDto.getEmail())
                .data(resDto)
                .build();
    }

    @PostMapping("/users/unblocking")
    public ResponseEntity<ApiResponse> unblockUser(@RequestBody BlockUserDto blockUserDto) {
        BlockUserDto resDto = followService.unBlockUser(blockUserDto);
        return  new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("unblock user email : " + resDto.getEmail())
                .data(resDto)
                .build();
    }

    @GetMapping("/followings/list")
    public ResponseEntity<ApiResponse> getFollowings(HttpServletRequest req) {
        List<FollowDto> followingList = followService.getFollowings(req);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get User Following List")
                .data(followingList)
                .build();
    }

    @GetMapping("/followers/list")
    public ResponseEntity<ApiResponse> getFollowers(HttpServletRequest req) {
        List<FollowDto> followerList = followService.getFollowers(req);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get User Follower List")
                .data(followerList)
                .build();
    }
}
