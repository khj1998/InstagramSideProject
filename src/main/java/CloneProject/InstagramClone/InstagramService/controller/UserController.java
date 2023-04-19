package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;
import CloneProject.InstagramClone.InstagramService.service.UserService;
import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;
import CloneProject.InstagramClone.InstagramService.entity.Member;
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
        userService.CreateUser(signUpDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Sign Up Success")
                .data(signUpDto)
                .build();
    }

    @GetMapping("/login/success")
    public ResponseEntity<ApiResponse> login(@RequestParam String username) {
        AuthResponse authResponse = userService.CreateJwtToken(username);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Login Success")
                .data(authResponse)
                .build();
    }

    @GetMapping("/login/failure")
    public ResponseEntity<ApiResponse> loginFailure() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Login Failure")
                .build();
    }

    @PostMapping("/access-token/re-allocation")
    public ResponseEntity<ApiResponse> allocateAccessToken(@RequestBody AuthDto authDto) {
        AuthResponse authResponse = userService.ReallocateAccessToken(authDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Reallocate Access Token")
                .data(authResponse)
                .build();
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