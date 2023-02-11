package CloneProject.InstagramClone.InstagramService.controller.exceptioncontroller;

import CloneProject.InstagramClone.InstagramService.exception.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.vo.SignUpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<SignUpResponse> handle() {
        SignUpResponse res = new SignUpResponse();
        res.setMessage("Email Exists");
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SignUpResponse> handleAnnotationException(){
        SignUpResponse res = new SignUpResponse();
        res.setMessage("FAILED");
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }
}
