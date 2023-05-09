package CloneProject.InstagramClone.InstagramService.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
