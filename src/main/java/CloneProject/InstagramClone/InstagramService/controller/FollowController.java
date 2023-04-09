package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowerDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowingDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<ApiResponse> addFollowing(@RequestBody FollowingDto followingDto) {
        FollowerDto resDto = followService.addFollowing(followingDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Following : "+ followingDto.getEmail())
                .data(resDto)
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addFollower(@RequestBody FollowerDto followerDto) {
        FollowerDto resDto = followService.addFollower(followerDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Follower : "+ followerDto.getEmail())
                .data(resDto)
                .build();
    }
}
