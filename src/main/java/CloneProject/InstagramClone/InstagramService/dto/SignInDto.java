package CloneProject.InstagramClone.InstagramService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class SignInDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
