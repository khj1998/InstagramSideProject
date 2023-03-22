package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import CloneProject.InstagramClone.InstagramService.service.UserService;
import CloneProject.InstagramClone.InstagramService.vo.AuthResponse;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.vo.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

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

    @PostMapping("/api/service")
    public ResponseEntity<ApiResponse> serviceTest() {
        log.info("api Service 도착");
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
    public ResponseEntity<ApiResponse> UpdateUser(@RequestBody @Validated UserEntity user) {
        return new ApiResponse.ApiResponseBuilder<>()
                .build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse> DeleteUser(@RequestBody UserEntity user) {
        return new ApiResponse.ApiResponseBuilder<>()
                .build();
    }
}
