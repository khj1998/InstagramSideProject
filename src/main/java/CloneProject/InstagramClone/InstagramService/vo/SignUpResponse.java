package CloneProject.InstagramClone.InstagramService.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class SignUpResponse {
    public String message;
}
