package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.exception.UserNotAuthenticated;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import CloneProject.InstagramClone.InstagramService.vo.AuthenticationResponse;
import CloneProject.InstagramClone.InstagramService.vo.Role;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.exception.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    public void createUser(SignUpDto signUpDto) {
        if (findUser(signUpDto.getEmail()) == null) {
            UserEntity user = setRoleToUser(signUpDto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.info("encoded password : {}",user.getPassword());
            userRepository.save(user);
        } else {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다!");
        }
    }

    @Override
    public AuthenticationResponse createJwtToken(HttpServletResponse res) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserEntity userEntity = userRepository.findByEmail(username);

        if (username.equals("anonymousUser")) {
            throw new UserNotAuthenticated("인증되지 않은 유저입니다.");
        }

        String accessToken = tokenProvider.generateAccessToken(userEntity);
        String refreshToken = tokenProvider.generateRefreshToken(userEntity);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private UserEntity findUser(String email) {
        return userRepository.findByEmail(email);
    }

    private UserEntity setRoleToUser(SignUpDto signUpDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity user = modelMapper.map(signUpDto, UserEntity.class);
        createRole(user);
        return user;
    }

    private void createRole(UserEntity user) {
        user.setRole(Role.ROLE_USER);
    }
}
