package CloneProject.InstagramClone.InstagramService.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    public String token;
}
