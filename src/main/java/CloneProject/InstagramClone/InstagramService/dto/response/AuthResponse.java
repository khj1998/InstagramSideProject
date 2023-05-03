package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"success","userId","message","accessToken","tokenType","expiresIn"})
public class AuthResponse {
    private final boolean success;
    private final Long userId;
    private final String accessToken;
    private final String tokenType;
    private final Integer expiresIn;
    private final String message;

    private AuthResponse(AuthResponseBuilder builder) {
        this.success = builder.success;
        this.userId = builder.userId;
        this.accessToken = builder.accessToken;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
        this.message = builder.message;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public Integer getExpiresIn() {
        return this.expiresIn;
    }

    public String getMessage() {
        return this.message;
    }

    public static class AuthResponseBuilder {
        private boolean success;
        private Long userId;
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String message;

        public AuthResponseBuilder(boolean success, String tokenType) {
            this.success = success;
            this.tokenType = tokenType;
        }

        public AuthResponseBuilder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AuthResponseBuilder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthResponseBuilder setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public AuthResponseBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ResponseEntity<AuthResponse> build() {
            AuthResponse authResponse = new AuthResponse(this);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(authResponse);
        }
    }
}
