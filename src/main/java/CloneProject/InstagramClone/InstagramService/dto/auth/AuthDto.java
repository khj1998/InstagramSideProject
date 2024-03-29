package CloneProject.InstagramClone.InstagramService.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDto {
    String accessToken;
    Long userId;
}
