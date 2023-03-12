package CloneProject.InstagramClone.InstagramService.controller.exceptioncontroller;

import CloneProject.InstagramClone.InstagramService.exception.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.exception.EmailNotExistsException;
import CloneProject.InstagramClone.InstagramService.exception.UserNotAuthenticated;
import CloneProject.InstagramClone.InstagramService.vo.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handle() {
        log.info("user already exists");
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Email Exists")
                .build();
    }

    @ExceptionHandler(EmailNotExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailNotExistsException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Email Not Exists")
                .build();
    }

    @ExceptionHandler(UserNotAuthenticated.class)
    public ResponseEntity<ApiResponse> handleUserPrincipalNotExistsException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("User Not Authenticated")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleAnnotationException(){
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("FAILED")
                .build();
    }
}
