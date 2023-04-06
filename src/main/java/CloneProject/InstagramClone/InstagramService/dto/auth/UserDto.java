package CloneProject.InstagramClone.InstagramService.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class UserDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Nullable
    private String nickname;
}
