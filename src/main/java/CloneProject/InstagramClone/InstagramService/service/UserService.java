package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.SignInDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.vo.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.file.attribute.UserPrincipalNotFoundException;

public interface UserService {
    void createUser(SignUpDto signUpDto);
    AuthenticationResponse createJwtToken(HttpServletResponse res);
}
