package CloneProject.InstagramClone.InstagramService.entity.comment;

import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL)
    private List<CommentLike> commentLikeList = new ArrayList<>();

    @Builder
    public Comment(Member member,Post post,String content) {
        this.member = member;
        this.post = post;
        this.content = content;
    }

    public void ChangeContent(String content) {
        this.content = content;
    }
}
