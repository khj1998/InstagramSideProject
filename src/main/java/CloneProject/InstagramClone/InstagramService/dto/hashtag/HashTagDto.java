package CloneProject.InstagramClone.InstagramService.dto.hashtag;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HashTagDto {
    private String tagName;
    private Date createdAt;
    private Long hashTagCount;
}
