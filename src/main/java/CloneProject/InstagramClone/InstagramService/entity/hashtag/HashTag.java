package CloneProject.InstagramClone.InstagramService.entity.hashtag;

import CloneProject.InstagramClone.InstagramService.exception.hashtag.NotHashTagEntityException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    private Long tagCount;

    @CreationTimestamp
    private Date createdAt;

    @OneToMany(mappedBy = "hashTag",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HashTagMapping> hashTagMappingList = new ArrayList<>();

    public void setHashTagMappingList(List<HashTagMapping> hashTagMappingList) {
        this.hashTagMappingList = hashTagMappingList;
    }

    @Builder
    public HashTag(String tagName,Long tagCount) {
        this.tagName = tagName;
        this.tagCount = tagCount;
    }

    public void AddTagCount() {
        this.tagCount+=1;
    }

    public void MinusTagCount() {
        this.tagCount-=1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HashTag)) {
            throw new NotHashTagEntityException("NotHashTagEntityException occurred");
        }
        return ((HashTag) obj).getTagName().equals(this.tagName);
    }

    @Override
    public int hashCode() {
        return 23 + Integer.parseInt(this.tagName);
    }
}
