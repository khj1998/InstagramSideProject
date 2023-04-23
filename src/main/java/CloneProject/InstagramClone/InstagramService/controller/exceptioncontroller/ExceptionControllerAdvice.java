package CloneProject.InstagramClone.InstagramService.controller.exceptioncontroller;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.JwtExResponse;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowLimitException;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.UnfollowFailedException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtSignatureException;
import CloneProject.InstagramClone.InstagramService.exception.user.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotAuthenticated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


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

    @ExceptionHandler(UserNotAuthenticated.class)
    public ResponseEntity<ApiResponse> handleUserPrincipalNotExistsException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("User Not Authenticated")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleAnnotationException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("FAILED")
                .build();
    }

    @ExceptionHandler(FollowLimitException.class)
    public ResponseEntity<ApiResponse> handleFollowLimitException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Follow Limit Exceeded")
                .build();
    }

    @ExceptionHandler(FollowMySelfException.class)
    public ResponseEntity<ApiResponse> followMySelfException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Follow Failed")
                .build();
    }

    @ExceptionHandler(UnfollowFailedException.class)
    public ResponseEntity<ApiResponse> unfollowFailedException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Unfollow Failed")
                .build();
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<JwtExResponse> handleJwtExpiredException() {
        return new JwtExResponse.AuthExResponseBuilder()
                .error("invalid_request")
                .error_description("Request with expired Json Web Token")
                .build();
    }

    @ExceptionHandler(JwtIllegalException.class)
    public ResponseEntity<JwtExResponse> handleJwtIllegalException() {
        return new JwtExResponse.AuthExResponseBuilder()
                .error("invalid_request")
                .error_description("Request with Illegal Json Web Token")
                .build();
    }

    @ExceptionHandler(JwtSignatureException.class)
    public ResponseEntity<JwtExResponse> handleJwtSignatureException() {
        return new JwtExResponse.AuthExResponseBuilder()
                .error("invalid_request")
                .error_description("Request with Invalid Json web Token Signature")
                .build();
    }
}
