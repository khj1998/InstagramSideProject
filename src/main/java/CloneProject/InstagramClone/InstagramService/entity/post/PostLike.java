package CloneProject.InstagramClone.InstagramService.entity.post;

import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "postlike")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {
    @Id
    @Column(name = "post_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostLike(Member member,Post post) {
        this.member = member;
        this.post = post;
    }
}
