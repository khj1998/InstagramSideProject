package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;

public interface UserService {
    void CreateUser(SignUpDto signUpDto);
    void ChangePassword();
    void logoutProcess(Long userId);
    AuthResponse ReallocateAccessToken(AuthDto authDto);
    AuthResponse CreateJwtToken(String username);

}
