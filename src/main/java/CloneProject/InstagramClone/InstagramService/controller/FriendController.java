package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.friend.FriendDto;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.service.friendservice.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @ApiOperation("친한 친구 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "친구 목록 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "본인을 차단한 유저 조회 실패")
    })
    @GetMapping("/all")
    public ResponseEntity<RestApiResponse> GetFriend(HttpServletRequest req) {
        return friendService.GetFriendList(req);
    }

    @ApiOperation("친한 친구 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "친한 친구 추가 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "친한 친구 추가 실패")
    })
    @PostMapping("/add")
    public ResponseEntity<RestApiResponse> AddFriend(@RequestBody List<FriendDto> friendDtoList) {
        return friendService.AddFriend(friendDtoList);
    }

    @ApiOperation("친한 친구 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "친한 친구 삭제 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "친한 친구 삭제 실패")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<RestApiResponse> DeleteFriend(@RequestBody List<FriendDto> friendDtoList) {
        return friendService.DeleteFriend(friendDtoList);
    }
}
