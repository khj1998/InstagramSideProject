package CloneProject.InstagramClone.InstagramService.dto.follow;

import lombok.Data;

import java.util.Date;

@Data
public class FriendDto {
    private String accessToken;
    private Long id;
    private String email;
    private Date createdAt;
}
