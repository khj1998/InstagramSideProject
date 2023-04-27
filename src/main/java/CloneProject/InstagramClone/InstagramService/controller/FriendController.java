package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> GetFriend(HttpServletRequest req) {
        List<FriendDto> resDto = friendService.GetFriendList(req);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get Friends")
                .data(resDto)
                .build();
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> AddFriend(@RequestBody List<FriendDto> friendDtoList) {
        List<FriendDto> resDto = friendService.AddFriend(friendDtoList);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Friends")
                .data(resDto)
                .build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> DeleteFriend(@RequestBody List<FriendDto> friendDtoList) {
        friendService.DeleteFriend(friendDtoList);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Delete Friends")
                .build();
    }
}
