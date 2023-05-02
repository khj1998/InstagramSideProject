package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success","message","data","updatedAt"})
public class ApiResponse<T> {
    public boolean success;
    public String message;
    public T data;
    public String updatedAt;

    private ApiResponse(ApiResponseBuilder<T> builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.data = builder.data;
        this.updatedAt = builder.updatedAt;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private String updatedAt;

        public ApiResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> updatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ResponseEntity<ApiResponse> build() {
            ApiResponse apiResponse = new ApiResponse(this);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(apiResponse);
        }
    }
}
