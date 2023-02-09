package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.config.SessionConst;
import CloneProject.InstagramClone.InstagramService.dto.UserDto;
import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.exception.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.service.UserService;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SignController {

    private final UserService userService;

    /*로그인 성공하면 세션 생성, 세션에 유저 객체를 담는다.*/
    @PostMapping("/{email}")
    public ResponseEntity<UserDto> SignInUser(@RequestBody @Validated UserDto signInDto, BindingResult bindingResult,
                                              HttpServletRequest req) {

        if (bindingResult.hasErrors()) {
            log.info("유효하지 않은 로그인 정보입니다!");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(signInDto);
        }

        HttpSession session = req.getSession(true);
        session.setAttribute(SessionConst.SessionName,signInDto);
        UserDto signInUser = (UserDto) session.getAttribute(SessionConst.SessionName);

        return ResponseEntity
               .ok()
               .body(signInDto);
    }

    @PostMapping("/add")
    public ResponseEntity<SignUpDto> SignUpUser(@RequestBody @Validated SignUpDto signUpDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("유효하지 않은 회원가입 정보입니다!");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(signUpDto);
        }

        try{
            userService.createUser(signUpDto);
        }catch (EmailAlreadyExistsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(signUpDto);
        }

        return ResponseEntity
                .ok()
                .body(signUpDto);
    }

    /*로그아웃 시 세션 객체 삭제*/
    @GetMapping("/signout")
    public ResponseEntity SignOutUser(HttpServletRequest req){

        HttpSession session = req.getSession(false);
        UserDto signInUser = (UserDto) session.getAttribute(SessionConst.SessionName);
        session.invalidate();

        return ResponseEntity
                .ok()
                .body(signInUser);
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserEntity> UpdateUser(@RequestBody @Validated UserEntity user, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    @DeleteMapping("/{email}")
    public String DeleteUser(@RequestBody UserEntity user) {
        return "Delete Process Complete!!";
    }
}
