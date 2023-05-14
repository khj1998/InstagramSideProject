package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.dto.response.AuthResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.ExceptionResponse;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomJwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        ObjectMapper objectMapper = new ObjectMapper();

        if (!requestURI.contains("/api/token/validation")) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            filterChain.doFilter(request, response);
            setValidTokenMessage(objectMapper,response);
        } catch (JwtIllegalException e) {
            log.info("IllegalJwtException : {}",e.getMessage());
            setIllegalExceptionResponse(objectMapper,response);
        } catch (JwtExpiredException e) {
            log.info("ExpiredTokenException : {}",e.getMessage());
            setExpiredExceptionResponse(objectMapper,response);
        } catch (SignatureException e) {
            log.info("SignatureException : {}",e.getMessage());
            setSignatureExceptionResponse(objectMapper,response);
        }
    }

    private void setValidTokenMessage(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        ResponseEntity authResponse = new AuthResponse.AuthResponseBuilder(true,"Bearer")
                .setMessage("valid Json Web Token")
                .build();
        String resValue = mapper.writeValueAsString(authResponse);
        res.getWriter().write(resValue);
    }

    private void setExpiredExceptionResponse(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        ResponseEntity jwtExResponse = new ExceptionResponse.ExceptionResponseBuilder(false)
                                    .setException("invalid_request")
                                    .setException_message("Request with expired Json Web Token")
                                    .build();
        String resValue = mapper.writeValueAsString(jwtExResponse);
        res.getWriter().write(resValue);
    }

    private void setIllegalExceptionResponse(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        ResponseEntity jwtExResponse = new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid_request")
                .setException_message("Request with Illegal Json Web Token")
                .build();
        String resValue = mapper.writeValueAsString(jwtExResponse);
        res.getWriter().write(resValue);
    }

    private void setSignatureExceptionResponse(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        ResponseEntity jwtExResponse = new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid_request")
                .setException_message("Request with Invalid Json web Token Signature")
                .build();
        String resValue = mapper.writeValueAsString(jwtExResponse);
        res.getWriter().write(resValue);
    }
}