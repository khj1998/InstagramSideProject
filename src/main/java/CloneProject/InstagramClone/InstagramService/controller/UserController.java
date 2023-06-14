package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;
import CloneProject.InstagramClone.InstagramService.service.userservice.UserService;
import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "회원가입 성공"),
            @ApiResponse(responseCode = "404",description = "회원가입 실패")
    })
    @PostMapping("/users/register")
    public ResponseEntity<RestApiResponse> SignUpUser(@RequestBody SignUpDto signUpDto) {
        return userService.SignUpUser(signUpDto);
    }

    @ApiOperation("유저 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "로그인 성공"),
            @ApiResponse(responseCode = "404",description = "로그인 실패")
    })
    @GetMapping("/login/success")
    public ResponseEntity<AuthResponse> login(@RequestParam String username) {
        return userService.LogInSuccessProcess(username);
    }

    @ApiOperation("로그인 실패")
    @GetMapping("/login/failure")
    public ResponseEntity<RestApiResponse> loginFailure() {
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Login Failure")
                .build();
    }

    @ApiOperation("토큰 재할당")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "토큰 재할당 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "토큰 재할당 실패")
    })
    @PostMapping("/access-token/re-allocation")
    public ResponseEntity<AuthResponse> allocateAccessToken(@RequestBody AuthDto authDto) {
        return userService.ReallocateAccessToken(authDto);
    }

    @ApiOperation("로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "로그아웃 성공")
    })
    @GetMapping("/users/logout")
    public ResponseEntity<RestApiResponse> logout(@RequestParam Long userId) {
        userService.logoutProcess(userId);
        return new RestApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Logout Success")
                .build();
    }

    @ApiOperation("유저 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "유저 정보 수정 성공")
    })
    @PutMapping("/{email}")
    public ResponseEntity<RestApiResponse> UpdateUser(@RequestBody @Validated Member user) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .build();
    }

    @ApiOperation("유저 회원탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "회원탈퇴 성공")
    })
    @DeleteMapping("/{email}")
    public ResponseEntity<RestApiResponse> DeleteUser(@RequestBody Member user) {
        return new RestApiResponse.ApiResponseBuilder<>()
                .build();
    }
}
