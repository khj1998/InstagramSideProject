package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.config.SpringConst;
import CloneProject.InstagramClone.InstagramService.dto.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.exception.*;
import CloneProject.InstagramClone.InstagramService.securitycustom.TokenProvider;
import CloneProject.InstagramClone.InstagramService.vo.AuthResponse;
import CloneProject.InstagramClone.InstagramService.vo.Role;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void CreateUser(SignUpDto signUpDto) {
        if (findUser(signUpDto.getEmail()) == null) {
            UserEntity user = setRoleToUser(signUpDto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다!");
        }
    }

    @Override
    public AuthResponse CreateJwtToken(String username) {
        UserEntity userEntity = userRepository.findByEmail(username);
        // 로그인 성공시, accessToken,refreshToken 발급.
        Long userId = userEntity.getId();
        String accessToken = tokenProvider.generateAccessToken(userEntity);
        String refreshToken = tokenProvider.generateRefreshToken(userEntity);
        redisTemplate.opsForValue().set(accessToken,username);
        redisTemplate.opsForValue().set(username,refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    /**
     * Refresh 토큰 유효시, Access Token,Refresh Token 재발급 후 응답 - RTR 방식 채택
     */
    @Override
    public AuthResponse ReallocateAccessToken(AuthDto authDto) {

        String username = (String) redisTemplate.opsForValue().get(authDto.getAccessToken());
        redisTemplate.delete(authDto.getAccessToken());
        UserEntity userEntity = userRepository.findByEmail(username);
        String refreshToken = (String) redisTemplate.opsForValue().get(username);
        String accessToken;

        try {
            //RefreshToken가 유효하지 않으면, 예외 발생
            tokenProvider.isRefreshTokenValid(refreshToken);
            accessToken = tokenProvider.generateAccessToken(userEntity);
            refreshToken = tokenProvider.generateRefreshToken(userEntity);
            redisTemplate.opsForValue().set(accessToken,username);
            redisTemplate.opsForValue().set(username,refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("RefreshToken Expired");
        } catch (IllegalArgumentException e) {
            throw new JwtIllegalException("Illegal Token");
        } catch (SignatureException e) {
            throw new JwtSignatureException("Illegal Signature");
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public void ChangePassword() {}

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

    @Override
    public void logoutProcess(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).get();
        String username = userEntity.getUsername();
        redisTemplate.delete(userId.toString());
    }
}
