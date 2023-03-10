package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class CustomJwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final int AFTER_BEARER = 7;
    private static final String JWT_TOKEN_PREFIX = "Bearer ";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/users/login",
            "POST");
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public CustomJwtFilter(AuthenticationManager authenticationManager,TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String username;
        String headerToken = parseToken(request);

        if (headerToken == null || !headerToken.startsWith(JWT_TOKEN_PREFIX)) {
            filterChain.doFilter(request,response);
            return;
        }

        //user not authenticated
        username = tokenProvider.extractUsername(headerToken);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() ==  null) {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream =  request.getInputStream();
            String bodyData = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            UserDto userDto = objectMapper.readValue(bodyData,UserDto.class);

            String email = userDto.getEmail();
            String password = userDto.getPassword();

            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(email,password);
            Authentication authResult = this.authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authResult);

            filterChain.doFilter(request,response);
        }
    }

    private String parseToken(HttpServletRequest req) {
        String jwtToken = req.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith("Bearer ")) {
            return jwtToken.substring(AFTER_BEARER);
        }
        return null;
    }
}
