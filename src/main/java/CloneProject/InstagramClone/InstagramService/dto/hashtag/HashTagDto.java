package CloneProject.InstagramClone.InstagramService.dto.hashtag;

import lombok.Data;

@Data
public class HashTagDto {
    private String accessToken;
    private Long postId;
    private String hashTag;
}
