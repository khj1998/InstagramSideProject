package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"error","error_description"})
public class JwtExResponse {
    private String exception;
    private String exception_description;

    private JwtExResponse(AuthExResponseBuilder builder) {
        this.exception = builder.exception;
        this.exception_description = builder.exception_description;
    }

    public String getException() {
        return this.exception;
    }

    public String getException_description() {
        return this.exception_description;
    }

    public static class AuthExResponseBuilder {
        private String exception;
        private String exception_description;

        public AuthExResponseBuilder error(String exception) {
            this.exception = exception;
            return this;
        }

        public AuthExResponseBuilder error_description(String exception_description) {
            this.exception_description = exception_description;
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
