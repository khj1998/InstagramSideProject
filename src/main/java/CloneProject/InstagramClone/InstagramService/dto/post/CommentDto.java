package CloneProject.InstagramClone.InstagramService.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private String accessToken;
    private Long postId;
    private Long commentId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
