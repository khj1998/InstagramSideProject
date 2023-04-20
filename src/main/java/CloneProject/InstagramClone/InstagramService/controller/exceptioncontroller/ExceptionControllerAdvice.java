package CloneProject.InstagramClone.InstagramService.controller.exceptioncontroller;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse> handleJwtExpiredException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Token Expired")
                .build();
    }

    @ExceptionHandler(JwtIllegalException.class)
    public ResponseEntity<ApiResponse> handleJwtIllegalException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Illegal Token")
                .build();
    }

    @ExceptionHandler(JwtSignatureException.class)
    public ResponseEntity<ApiResponse> handleJwtSignatureException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("Illegal Signature")
                .build();
    }
}
