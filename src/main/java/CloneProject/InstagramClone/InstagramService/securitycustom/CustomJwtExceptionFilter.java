package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.exception.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.vo.response.TokenMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper objectMapper = new ObjectMapper();

        if (!requestURI.contains("/api/authorization")) {
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

        TokenMessage message = new TokenMessage();
        message.setMessage("Valid Token");
        String resValue = mapper.writeValueAsString(message);
        res.getWriter().write(resValue);
    }

    private void setExpiredExceptionResponse(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        TokenMessage message = new TokenMessage();
        message.setMessage("Expired Access Token");
        String resValue = mapper.writeValueAsString(message);
        res.getWriter().write(resValue);
    }

    private void setIllegalExceptionResponse(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        TokenMessage message = new TokenMessage();
        message.setMessage("Illegal Token");
        String resValue = mapper.writeValueAsString(message);
        res.getWriter().write(resValue);
    }

    private void setSignatureExceptionResponse(ObjectMapper mapper, HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.OK.value());
        res.setContentType("application/json; charset=UTF-8");

        TokenMessage message = new TokenMessage();
        message.setMessage("Invalid Signature");
        String resValue = mapper.writeValueAsString(message);
        res.getWriter().write(resValue);
    }
}