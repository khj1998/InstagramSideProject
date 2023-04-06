package CloneProject.InstagramClone.InstagramService.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.ResponseEntity;

@JsonPropertyOrder({"success","message","data"})
public class ApiResponse<T> {
    public boolean success;
    public String message;
    public T data;

    private ApiResponse(ApiResponseBuilder<T> builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.data = builder.data;
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

        public ResponseEntity<ApiResponse> build() {
            ApiResponse apiResponse = new ApiResponse(this);
            return ResponseEntity.ok(apiResponse);
        }
    }
}
