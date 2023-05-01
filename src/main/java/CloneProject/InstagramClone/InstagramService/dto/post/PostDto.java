package CloneProject.InstagramClone.InstagramService.dto.post;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long id;
    private String accessToken;
    private String title;
    private String content;
    private String imageUrl;
    private List<HashTagDto> hashTagList = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
}
