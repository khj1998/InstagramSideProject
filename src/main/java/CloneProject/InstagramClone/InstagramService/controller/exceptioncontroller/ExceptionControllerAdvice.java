package CloneProject.InstagramClone.InstagramService.controller.exceptioncontroller;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.FollowExResponse;
import CloneProject.InstagramClone.InstagramService.dto.response.JwtExResponse;
import CloneProject.InstagramClone.InstagramService.exception.follow.BlockMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowLimitException;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.UnfollowFailedException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtSignatureException;
import CloneProject.InstagramClone.InstagramService.exception.user.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotAuthenticated;
import CloneProject.InstagramClone.InstagramService.exception.user.UserNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("user not found exception")
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
    public ResponseEntity<FollowExResponse> handleAnnotationException() {
        return new FollowExResponse.FollowExResponseBuilder(false)
                .setException("")
                .setException_message("")
                .build();
    }

    @ExceptionHandler(FollowLimitException.class)
    public ResponseEntity<FollowExResponse> handleFollowLimitException() {
        return new FollowExResponse.FollowExResponseBuilder(false)
                .setException("invalid follow request")
                .setException_message("following limit exceeded")
                .build();
    }

    @ExceptionHandler(FollowMySelfException.class)
    public ResponseEntity<FollowExResponse> handleFollowMySelfException() {
        return new FollowExResponse.FollowExResponseBuilder(false)
                .setException("invalid follow request")
                .setException_message("following my account is not valid")
                .build();
    }

    @ExceptionHandler(UnfollowFailedException.class)
    public ResponseEntity<FollowExResponse> handleUnfollowFailedException() {
        return new FollowExResponse.FollowExResponseBuilder(false)
                .setException("invalid unfollow request")
                .setException_message("Unfollow failed exception occurred")
                .build();
    }

    @ExceptionHandler(BlockMySelfException.class)
    public ResponseEntity<ApiResponse> handleBlockMySelfException() {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(false)
                .message("block my account exception")
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
