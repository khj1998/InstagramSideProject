package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
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
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (!requestURI.contains("/api/authorization")) {
            filterChain.doFilter(request, response);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream;
        String tokenBody;
        TokenDto tokenDto;

        //검증 프로세스
        try {
            inputStream = request.getInputStream();
            tokenBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            tokenDto = objectMapper.readValue(tokenBody, TokenDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            tokenProvider.isTokenValid(tokenDto.getAccessToken());
        } catch(ExpiredJwtException e) {
            log.info("만료된 JWT 토큰 재발급 필요!!");
        }

        filterChain.doFilter(request, response);
    }
}
