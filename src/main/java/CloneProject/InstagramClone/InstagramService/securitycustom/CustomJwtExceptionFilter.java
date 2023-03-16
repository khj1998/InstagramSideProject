package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.exception.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.JwtIllegalException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class CustomJwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (!requestURI.contains("/api/authorization")) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } catch (JwtIllegalException e) {
            log.info("IllegalJwtException : {}",e.getMessage());
            setIllegalExceptionResponse(response);
        } catch (JwtExpiredException e) {
            log.info("ExpiredTokenException : {}",e.getMessage());
            setExpiredExceptionResponse(response);
        } catch (SignatureException e) {
            log.info("SignatureException : {}",e.getMessage());
            setSignatureExceptionResponse(response);
        }
    }

    private void setExpiredExceptionResponse(HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write("Expired Access Token");
    }

    private void setIllegalExceptionResponse(HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write("Expired Access Token");
    }

    private void setSignatureExceptionResponse(HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write("Expired Access Token");
    }
}