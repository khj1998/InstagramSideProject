package CloneProject.InstagramClone.InstagramService.dto.auth;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignInDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
