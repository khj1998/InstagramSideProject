package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;
import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;

public interface UserService {
    void CreateUser(SignUpDto signUpDto);
    void ChangePassword();
    void logoutProcess(Long userId);
    AuthResponse ReallocateAccessToken(AuthDto authDto);
    AuthResponse CreateJwtToken(String username);
}
