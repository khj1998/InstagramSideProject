package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
