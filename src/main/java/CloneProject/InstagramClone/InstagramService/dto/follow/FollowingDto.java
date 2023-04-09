package CloneProject.InstagramClone.InstagramService.dto.follow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FollowingDto {
    private String accessToken;
    private Long followingId;
    private String email;
    private String nickname;
    private Date followingAt;
}
