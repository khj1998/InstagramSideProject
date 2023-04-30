package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtSignatureException;
import CloneProject.InstagramClone.InstagramService.exception.user.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotFoundException;
import CloneProject.InstagramClone.InstagramService.entity.member.Role;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void CreateUser(SignUpDto signUpDto) {
        if (findUser(signUpDto.getEmail()) == null) {
            Member user = setRoleToUser(signUpDto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            memberRepository.save(user);
        } else {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다!");
        }
    }

    @Override
    public String CreateJwtToken(String username) {
        Member member = memberRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException occurred"));

        // 로그인 성공시, accessToken,refreshToken 발급.
        String accessToken = tokenService.generateAccessToken(member);
        String refreshToken = tokenService.generateRefreshToken(member);
        redisTemplate.opsForValue().set(username,refreshToken);

        return accessToken;
    }

    /**
     * Refresh 토큰 유효시, Access Token,Refresh Token 재발급 후 응답 - RTR 방식 채택
     * Refresh 토큰이 만료되었다면, 로그인 세션이 만료된 상황 => 로그아웃 프로세스 진행
     */
    @Override
    @Transactional
    public String ReallocateAccessToken(AuthDto authDto) {

        String username = tokenService.extractUsername(authDto.getAccessToken());
        if (username == null) {
            throw new JwtExpiredException("Invalid AccessToken");
        }
        
        Member member = memberRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException occurred"));
        String refreshToken = (String) redisTemplate.opsForValue().get(username);
        String accessToken;

        try {
            //RefreshToken가 유효하지 않으면, 예외 발생
            tokenService.isRefreshTokenValid(refreshToken);
            accessToken = tokenService.generateAccessToken(member);
            refreshToken = tokenService.generateRefreshToken(member);
            redisTemplate.opsForValue().set(username,refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("RefreshToken Expired");
        } catch (IllegalArgumentException e) {
            throw new JwtIllegalException("Illegal Token");
        } catch (SignatureException e) {
            throw new JwtSignatureException("Illegal Signature");
        }

        return accessToken;
    }

    @Override
    public void ChangePassword() {}

    private Member findUser(String email) {
        return memberRepository
                .findByEmail(email)
                .orElse(null);
    }

    private Member setRoleToUser(SignUpDto signUpDto) {
        Member user = modelMapper.map(signUpDto, Member.class);
        createRole(user);
        return user;
    }

    private void createRole(Member user) {
        user.setRole(Role.ROLE_USER);
    }

    @Override
    public void logoutProcess(Long userId) {
        Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("UserNotFoundException occurred"));
        String username = member.getUsername();
        redisTemplate.delete(username);
    }
}
