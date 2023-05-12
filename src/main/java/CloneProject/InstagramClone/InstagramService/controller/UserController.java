package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;
import CloneProject.InstagramClone.InstagramService.service.userservice.UserService;
import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
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

    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse> SignUpUser(@RequestBody SignUpDto signUpDto) {
        return userService.CreateUser(signUpDto);
    }

    @GetMapping("/login/success")
    public ResponseEntity<AuthResponse> login(@RequestParam String username) {
        return userService.LogInSuccessProcess(username);
    }

    @GetMapping("/login/failure")
    public ResponseEntity<ApiResponse> loginFailure() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Login Failure")
                .build();
    }

    @PostMapping("/access-token/re-allocation")
    public ResponseEntity<AuthResponse> allocateAccessToken(@RequestBody AuthDto authDto) {
        return userService.ReallocateAccessToken(authDto);
    }

    @GetMapping("/service")
    public ResponseEntity<ApiResponse> serviceTest(@RequestParam("token") String accessToken) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Service Api Response")
                .data(null)
                .build();
    }

    @GetMapping("/users/logout")
    public ResponseEntity<ApiResponse> logout(@RequestParam Long userId) {
        userService.logoutProcess(userId);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Logout Success")
                .build();
    }

    @PutMapping("/{email}")
    public ResponseEntity<ApiResponse> UpdateUser(@RequestBody @Validated Member user) {
        return new ApiResponse.ApiResponseBuilder<>()
                .build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse> DeleteUser(@RequestBody Member user) {
        return new ApiResponse.ApiResponseBuilder<>()
                .build();
    }
}
