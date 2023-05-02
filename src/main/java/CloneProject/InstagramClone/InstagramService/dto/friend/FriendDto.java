package CloneProject.InstagramClone.InstagramService.dto.friend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendDto {
    private String accessToken;
    private Long id;
    private Date createdAt;
}
