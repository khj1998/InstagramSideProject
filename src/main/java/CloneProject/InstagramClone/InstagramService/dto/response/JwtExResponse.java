package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"error","error_description"})
public class JwtExResponse {
    private String error;
    private String error_description;

    private JwtExResponse(AuthExResponseBuilder builder) {
        this.error = builder.error;
        this.error_description = builder.error_description;
    }

    public String getError() {
        return this.error;
    }

    public String getError_description() {
        return this.error_description;
    }

    public static class AuthExResponseBuilder {
        private String error;
        private String error_description;

        public AuthExResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public AuthExResponseBuilder error_description(String error_description) {
            this.error_description = error_description;
            return this;
        }

        public ResponseEntity<JwtExResponse> build() {
            JwtExResponse jwtExResponse = new JwtExResponse(this);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(jwtExResponse);
        }
    }
}
