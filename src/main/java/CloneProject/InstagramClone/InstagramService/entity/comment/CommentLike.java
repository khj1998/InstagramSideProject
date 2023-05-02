package CloneProject.InstagramClone.InstagramService.entity.comment;

import CloneProject.InstagramClone.InstagramService.entity.comment.Comment;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "commentlike")
@NoArgsConstructor
public class CommentLike {
    @Id
    @Column(name = "comment_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
