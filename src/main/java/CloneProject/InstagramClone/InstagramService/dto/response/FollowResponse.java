package CloneProject.InstagramClone.InstagramService.dto.response;

import CloneProject.InstagramClone.InstagramService.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"success","message","fromMember","toMember"})
public class FollowResponse<T> {
    private boolean success;
    private String message;
    private T fromMember;
    private T toMember;

    private FollowResponse(FollowResponseBuilder<T> builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.fromMember = builder.fromMember;
        this.toMember = builder.toMember;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public T getFromMember() {
        return this.fromMember;
    }

    public T getToMember() {
        return this.toMember;
    }

    public static class FollowResponseBuilder<T> {
        private boolean success;
        private String message;
        private T fromMember;
        private T toMember;

        public FollowResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public FollowResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public FollowResponseBuilder<T> fromMember(T fromMember) {
            this.fromMember = fromMember;
            return this;
        }

        public FollowResponseBuilder<T> toMember(T toMember) {
            this.toMember = toMember;
            return this;
        }

        public ResponseEntity<FollowResponse> build() {
            FollowResponse apiResponse = new FollowResponse(this);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(apiResponse);
        }
    }
}
