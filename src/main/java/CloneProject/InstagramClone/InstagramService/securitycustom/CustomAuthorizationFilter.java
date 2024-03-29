package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.dto.auth.AuthDto;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Access Token 요청을 받는 필터
 * Access Token이 유효한지 검증하고, 유효하면 URL에 맞는 서비스로 이동
 * 유효하지 않다면 반려. 만료된 토큰이라면 토큰 갱신 응답.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (!requestURI.contains("/api/token/validation")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream;
        String tokenBody;
        AuthDto authDto;

        //검증 프로세스
        try {
            inputStream = request.getInputStream();
            tokenBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            authDto = objectMapper.readValue(tokenBody, AuthDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            tokenService.isTokenValid(authDto.getAccessToken());
        } catch(IllegalArgumentException e) {
            throw new JwtIllegalException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("만료된 토큰입니다.");
        } catch (SignatureException e) {
            throw new SignatureException("올바르지 않은 인증입니다.");
        }
    }
}
