package CloneProject.InstagramClone.InstagramService.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    public Long userId;
    public String accessToken;
    public String refreshToken;
}
