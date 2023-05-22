package CloneProject.InstagramClone.InstagramService.service.userservice;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.UserDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ApiResponse> SignUpUser(SignUpDto signUpDto);
    void ChangePassword();
    void logoutProcess(Long userId);
    ResponseEntity<AuthResponse> ReallocateAccessToken(AuthDto authDto);
    ResponseEntity<AuthResponse> LogInSuccessProcess(String username);
}
