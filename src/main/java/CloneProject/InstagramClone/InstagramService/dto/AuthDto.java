package CloneProject.InstagramClone.InstagramService.dto;

import lombok.Data;

@Data
public class AuthDto {
    Long userId;
    String accessToken;
    String refreshToken;
}
