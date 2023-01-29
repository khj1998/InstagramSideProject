package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.domain.User;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import CloneProject.InstagramClone.InstagramService.service.InstagramService;
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

    @GetMapping("/{id}")
    public ResponseEntity<User> FindUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    @PostMapping("/{id}")
    public ResponseEntity<User> CreateUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        return ResponseEntity
               .ok()
               .body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> UpdateUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    @DeleteMapping("/{id}")
    public String DeleteUser(@RequestBody User user) {
        return "Delete Process Complete!!";
    }
}
