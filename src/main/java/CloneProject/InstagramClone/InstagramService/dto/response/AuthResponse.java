package CloneProject.InstagramClone.InstagramService.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    public String accessToken;
}