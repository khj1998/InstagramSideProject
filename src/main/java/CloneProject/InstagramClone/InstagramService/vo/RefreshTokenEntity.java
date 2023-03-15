package CloneProject.InstagramClone.InstagramService.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken",timeToLive = 3600)
@JsonSerialize
public class RefreshTokenEntity {
    @Id
    private final Long userId;
    private final String refreshToken;

    public RefreshTokenEntity(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}
