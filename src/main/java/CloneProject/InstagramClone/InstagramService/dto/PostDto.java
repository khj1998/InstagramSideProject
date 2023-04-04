package CloneProject.InstagramClone.InstagramService.dto;

import CloneProject.InstagramClone.InstagramService.entity.Member;
import lombok.Data;

import java.util.Date;

@Data
public class PostDto {
    private String accessToken;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
