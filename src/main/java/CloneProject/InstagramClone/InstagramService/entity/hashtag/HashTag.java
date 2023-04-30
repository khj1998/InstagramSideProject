package CloneProject.InstagramClone.InstagramService.entity.hashtag;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@Table(name = "hashtags")
@NoArgsConstructor
public class HashTag {
    @Id
    @Column(name = "hashtag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tagName;

    @CreationTimestamp
    private Date createdAt;

    @OneToMany(mappedBy = "hashTag",cascade = CascadeType.ALL)
    private List<HashTagMapping> hashTagMappingList = new ArrayList<>();

    @Builder
    public HashTag(String tagName) {
        this.tagName = tagName;
    }
}
