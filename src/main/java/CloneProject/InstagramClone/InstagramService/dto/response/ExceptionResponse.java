package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"success","exception","exception_message"})
public class ExceptionResponse {
    private boolean success;
    private String exception;
    private String exception_message;

    private ExceptionResponse(ExceptionResponseBuilder builder) {
        this.success = builder.success;
        this.exception = builder.exception;
        this.exception_message = builder.exception_message;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getException() {
        return this.exception;
    }

    public String getException_message() {
        return this.exception_message;
    }

    public static class ExceptionResponseBuilder {
        private boolean success;
        private String exception;
        private String exception_message;

        public ExceptionResponseBuilder(boolean success) {
            this.success = success;
        }

        public ExceptionResponseBuilder setException(String exception) {
            this.exception = exception;
            return this;
        }

        public ExceptionResponseBuilder setException_message(String exception_message) {
            this.exception_message = exception_message;
            return this;
        }

        public ResponseEntity<ExceptionResponse> build() {
            ExceptionResponse response = new ExceptionResponse(this);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(response);
        }
    }
}
