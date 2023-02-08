package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.config.SessionConst;
import CloneProject.InstagramClone.InstagramService.vo.User;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import CloneProject.InstagramClone.InstagramService.service.InstagramService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class SignController {

    private final InstagramService instagramService;
    private final UserRepository userRepository;

    @GetMapping("/{email}")
    public ResponseEntity<User> FindUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    /*로그인 성공하면 세션 생성, 세션에 유저 객체를 담는다.*/
    @PostMapping("/{email}")
    public ResponseEntity<User> SignInUser(@RequestBody @Validated User user, BindingResult bindingResult,
                                           HttpServletRequest req) {

        HttpSession session = req.getSession(true);
        session.setAttribute(SessionConst.SessionName,user);
        User user2 = (User) session.getAttribute(SessionConst.SessionName);
        log.info(user2.getEmail());

        return ResponseEntity
               .ok()
               .body(null);
    }

    /*로그아웃 시 세션 객체 삭제*/
    @GetMapping("/signout")
    public String SignOutUser(HttpServletRequest req){

        HttpSession session = req.getSession(false);
        User user2 = (User) session.getAttribute(SessionConst.SessionName);
        log.info(user2.getEmail());
        //session.invalidate();
        log.info("signout process!!!");

        return "SignOut Complete!!";
    }

    @PutMapping("/{email}")
    public ResponseEntity<User> UpdateUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    @DeleteMapping("/{email}")
    public String DeleteUser(@RequestBody User user) {
        return "Delete Process Complete!!";
    }

    @PostMapping("/add")
    public ResponseEntity<User> SignUpUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }
}
