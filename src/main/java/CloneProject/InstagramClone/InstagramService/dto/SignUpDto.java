package CloneProject.InstagramClone.InstagramService.dto;

import CloneProject.InstagramClone.InstagramService.validator.PasswordLengthChecker;
import CloneProject.InstagramClone.InstagramService.validator.PasswordMatcher;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@PasswordMatcher
@PasswordLengthChecker
public class SignUpDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
    private String passwordChecker;

    @Nullable
    private String nickname;
}
