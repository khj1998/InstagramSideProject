package CloneProject.InstagramClone.InstagramService.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    @Email
    private String email;

    @NotBlank
    private String password;

    @Nullable
    private String nickname;
}
