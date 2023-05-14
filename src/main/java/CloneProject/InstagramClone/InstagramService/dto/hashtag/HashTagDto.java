package CloneProject.InstagramClone.InstagramService.dto.hashtag;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class HashTagDto {
    private String tagName;
    private Date createdAt;
    private Long hashTagCount;

    @Builder
    public HashTagDto(String tagName) {
        this.tagName = tagName;
    }
}
