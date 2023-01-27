package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.domain.SignIn;
import CloneProject.InstagramClone.InstagramService.domain.SignUp;
import CloneProject.InstagramClone.InstagramService.service.InstagramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/instagram")
@RequiredArgsConstructor
public class SignController {

    private final InstagramService instagramService;

    public ResponseEntity<SignIn> SignInProcess(@RequestBody @Validated SignIn signIn, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    public ResponseEntity<SignUp> SignUpProcess(@RequestBody @Validated SignUp signUp, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }
}
