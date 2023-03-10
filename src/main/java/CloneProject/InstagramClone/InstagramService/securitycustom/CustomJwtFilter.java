package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.config.SpringConst;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
public class CustomJwtFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;
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
        String username;
        UsernamePasswordAuthenticationToken authToken;
        String headerToken = parseToken(request);

        if (headerToken == null || !headerToken.startsWith(SpringConst.JWT_TOKEN_PREFIX)) {
            filterChain.doFilter(request,response);
            return;
        }

        //user not authenticated
        username = tokenProvider.extractUsername(headerToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() ==  null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                authToken = new UsernamePasswordAuthenticationToken(username,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request,response);
        }
    }

    private String parseToken(HttpServletRequest req) {
        String jwtToken = req.getHeader(SpringConst.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith("Bearer ")) {
            return jwtToken.substring(SpringConst.AFTER_BEARER);
        }
        return null;
    }
}
