package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.vo.TokenResponse;

public interface UserService {
    void createUser(SignUpDto signUpDto);
    TokenResponse createJwtToken(String username);
}
