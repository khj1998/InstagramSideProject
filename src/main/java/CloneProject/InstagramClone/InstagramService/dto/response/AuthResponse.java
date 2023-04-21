package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@JsonPropertyOrder({"success","accessToken","tokenType","expiresIn","scope"})
public class AuthResponse {
    public boolean success;
    public String accessToken;
    public String tokenType;
    public int expiresIn;
    public String scope;

    private AuthResponse(AuthResponseBuilder builder) {
        this.success = builder.success;
        this.accessToken = builder.accessToken;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
        this.scope = builder.scope;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }

    public String getScope() {
        return this.scope;
    }

    public static class AuthResponseBuilder {
        private boolean success;
        private String accessToken;
        private String tokenType;
        private int expiresIn;
        private String scope;

        public AuthResponseBuilder(boolean success, String accessToken, String tokenType) {
            this.success = success;
            this.accessToken = accessToken;
            this.tokenType = tokenType;
        }

        public AuthResponseBuilder setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public AuthResponseBuilder setScope(String scope) {
            this.scope = scope;
            return this;
        }

        public ResponseEntity<AuthResponse> build() {
            AuthResponse authResponse = new AuthResponse(this);
            return ResponseEntity.ok(authResponse);
        }
    }
}
