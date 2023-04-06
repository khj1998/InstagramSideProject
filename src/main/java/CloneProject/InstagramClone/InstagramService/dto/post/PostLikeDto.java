package CloneProject.InstagramClone.InstagramService.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostLikeDto {
    private String accessToken;
    private Long postId;
    private String postTitle;
}
