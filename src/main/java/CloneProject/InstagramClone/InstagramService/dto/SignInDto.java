package CloneProject.InstagramClone.InstagramService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
