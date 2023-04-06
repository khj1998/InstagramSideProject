package CloneProject.InstagramClone.InstagramService.dto.post;

import CloneProject.InstagramClone.InstagramService.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long id;
    private String accessToken;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
