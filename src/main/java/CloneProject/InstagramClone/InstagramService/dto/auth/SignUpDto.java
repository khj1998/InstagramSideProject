package CloneProject.InstagramClone.InstagramService.dto.auth;

import CloneProject.InstagramClone.InstagramService.validator.PasswordLengthChecker;
import CloneProject.InstagramClone.InstagramService.validator.PasswordMatcher;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@PasswordMatcher
@PasswordLengthChecker
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
    private String passwordChecker;

    @Nullable
    private String nickname;
}
