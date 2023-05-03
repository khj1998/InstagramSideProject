package CloneProject.InstagramClone.InstagramService.controller.exceptioncontroller;

import CloneProject.InstagramClone.InstagramService.dto.response.ExceptionResponse;
import CloneProject.InstagramClone.InstagramService.exception.comment.CommentNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.follow.BlockMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowLimitException;
import CloneProject.InstagramClone.InstagramService.exception.follow.FollowMySelfException;
import CloneProject.InstagramClone.InstagramService.exception.follow.UnfollowFailedException;
import CloneProject.InstagramClone.InstagramService.exception.friend.DuplicatedFriendException;
import CloneProject.InstagramClone.InstagramService.exception.friend.FriendMinSelectException;
import CloneProject.InstagramClone.InstagramService.exception.friend.FriendNoFoundException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagLimitException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNameNotValidException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.NotHashTagEntityException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtSignatureException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.user.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.exception.user.IllegalUserIdException;
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
    public ResponseEntity<ExceptionResponse> handleEmailAlreadyExistsException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid signup request")
                .setException_message("email already exists exception")
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid request")
                .setException_message("user not found exception")
                .build();
    }

    @ExceptionHandler(UserNotAuthenticated.class)
    public ResponseEntity<ExceptionResponse> handleUserPrincipalNotExistsException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid authentication request")
                .setException_message("user principal not exists")
                .build();
    }

    @ExceptionHandler(IllegalUserIdException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalUserIdException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid user id request")
                .setException_message("user id must not be null")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleAnnotationException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("")
                .setException_message("")
                .build();
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePostNotFoundException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid post request")
                .setException_message("failed to find post")
                .build();
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCommentNotFoundException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid comment request")
                .setException_message("failed to find comment")
                .build();
    }

    @ExceptionHandler(FollowLimitException.class)
    public ResponseEntity<ExceptionResponse> handleFollowLimitException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid follow request")
                .setException_message("following limit exceeded")
                .build();
    }

    @ExceptionHandler(FollowMySelfException.class)
    public ResponseEntity<ExceptionResponse> handleFollowMySelfException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid follow request")
                .setException_message("following my account is not valid")
                .build();
    }

    @ExceptionHandler(UnfollowFailedException.class)
    public ResponseEntity<ExceptionResponse> handleUnfollowFailedException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid unfollow request")
                .setException_message("Unfollow failed exception occurred")
                .build();
    }

    @ExceptionHandler(BlockMySelfException.class)
    public ResponseEntity<ExceptionResponse> handleBlockMySelfException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid user blocking request")
                .setException_message("block myself request is not valid")
                .build();
    }

    @ExceptionHandler(FriendMinSelectException.class)
    public ResponseEntity<ExceptionResponse> handleFriendMinSelectException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid friend add request")
                .setException_message("At least one friend must be selected")
                .build();
    }

    @ExceptionHandler(FriendNoFoundException.class)
    public ResponseEntity<ExceptionResponse> handleFriendNoFoundException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid friend delete request")
                .setException_message("friend not exists")
                .build();
    }

    @ExceptionHandler(DuplicatedFriendException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicatedFriendException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid add friend request")
                .setException_message("Duplicated friend")
                .build();
    }

    @ExceptionHandler(HashTagNameNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleHashTagNameNotValidException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid hash tag request")
                .setException_message("Hashtag name must be started with #")
                .build();
    }

    @ExceptionHandler(HashTagLimitException.class)
    public ResponseEntity<ExceptionResponse> handleHashTagLimitException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid hash tag numbers")
                .setException_message("HastTag limit(30) exceeded")
                .build();
    }

    @ExceptionHandler(HashTagNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleHashTagNotFoundException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid hashtag request")
                .setException_message("Such HashTag not found")
                .build();
    }

    @ExceptionHandler(NotHashTagEntityException.class)
    public ResponseEntity<ExceptionResponse> handleNotHashTagEntityException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid entity request")
                .setException_message("Hashtag Entity required")
                .build();
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleJwtExpiredException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid_request")
                .setException_message("Request with expired Json Web Token")
                .build();
    }

    @ExceptionHandler(JwtIllegalException.class)
    public ResponseEntity<ExceptionResponse> handleJwtIllegalException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid_request")
                .setException_message("Request with Illegal Json Web Token")
                .build();
    }

    @ExceptionHandler(JwtSignatureException.class)
    public ResponseEntity<ExceptionResponse> handleJwtSignatureException() {
        return new ExceptionResponse.ExceptionResponseBuilder(false)
                .setException("invalid_request")
                .setException_message("Request with Invalid Json web Token Signature")
                .build();
    }
}
