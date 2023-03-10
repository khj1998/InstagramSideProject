package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.SignInDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.vo.AuthenticationResponse;

public interface UserService {
    void createUser(SignUpDto signUpDto);
    AuthenticationResponse authentication(SignInDto signInDto);
}
