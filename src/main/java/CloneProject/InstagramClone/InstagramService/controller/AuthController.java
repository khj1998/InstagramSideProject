package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.SignInDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.service.UserService;
import CloneProject.InstagramClone.InstagramService.vo.AuthenticationResponse;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.vo.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> SignUpUser(@RequestBody SignUpDto signUpDto) {
        userService.createUser(signUpDto);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Sign Up Success")
                .data(signUpDto)
                .build();
    }

    @PostMapping("/sign")
    public ResponseEntity<ApiResponse> SignInUser(HttpServletResponse res) {
        AuthenticationResponse authResponse = userService.createJwtToken(res);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Sign In Success")
                .data(authResponse)
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
