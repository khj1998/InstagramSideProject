package CloneProject.InstagramClone.InstagramService.dto.follow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FollowDto {
    private String accessToken;
    private Long id;
    private String nickname;
    private Date createdAt;
}
