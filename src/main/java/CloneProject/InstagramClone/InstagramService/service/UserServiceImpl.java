package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.config.SpringConst;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.exception.UserNotAuthenticated;
import CloneProject.InstagramClone.InstagramService.repository.RefreshTokenRepository;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import CloneProject.InstagramClone.InstagramService.vo.RefreshTokenEntity;
import CloneProject.InstagramClone.InstagramService.vo.TokenResponse;
import CloneProject.InstagramClone.InstagramService.vo.Role;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.exception.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void createUser(SignUpDto signUpDto) {
        if (findUser(signUpDto.getEmail()) == null) {
            UserEntity user = setRoleToUser(signUpDto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다!");
        }
    }

    @Override
    public TokenResponse createJwtToken(String username) {
        Authentication authentication = SpringConst.AUTH_REPOSITORY.get(username);
        log.info("{}", authentication);

        if (authentication == null) {
            throw new UserNotAuthenticated("인증되지 않은 유저입니다.");
        }

        UserEntity userEntity = userRepository.findByEmail(username);
        String accessToken = tokenProvider.generateAccessToken(userEntity);
        String refreshToken = tokenProvider.generateRefreshToken(userEntity);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByUserId(userEntity.getId());

        if (refreshTokenEntity == null) {
            RefreshTokenEntity saveToken = new RefreshTokenEntity(userEntity.getId(),refreshToken);
            refreshTokenRepository.save(saveToken);
        } else {
            log.info("userId : {}, refreshToken : {}"
                    , refreshTokenEntity.getRefreshToken(), refreshTokenEntity.getRefreshToken());
        }

        return TokenResponse.builder()
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
