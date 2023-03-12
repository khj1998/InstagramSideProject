package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.service.UserService;
import CloneProject.InstagramClone.InstagramService.vo.AuthenticationResponse;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.vo.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse> SignUpUser(@RequestBody SignUpDto signUpDto) {
        userService.createUser(signUpDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Sign Up Success")
                .data(signUpDto)
                .build();
    }

    @GetMapping("/login/success")
    public ResponseEntity<ApiResponse> login(@RequestParam String username,HttpServletResponse res) {
        AuthenticationResponse authResponse = userService.createJwtToken(username,res);
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
