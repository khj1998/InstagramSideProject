package CloneProject.InstagramClone.InstagramService.dto.follow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockUserDto {
    private String accessToken;
    private Long banId;
    private String email;
    private Date createdAt;
}
