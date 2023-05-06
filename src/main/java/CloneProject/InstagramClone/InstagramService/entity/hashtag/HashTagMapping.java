package CloneProject.InstagramClone.InstagramService.entity.hashtag;

import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "hashtag_mapping_table")
@NoArgsConstructor
public class HashTagMapping {
    @Id
    @Column(name = "hashtag_table_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "post_fk")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "hashtag_fk")
    private HashTag hashTag;

    @Builder
    public HashTagMapping(Post post, HashTag hashTag) {
        this.post = post;
        this.hashTag = hashTag;
    }
}
