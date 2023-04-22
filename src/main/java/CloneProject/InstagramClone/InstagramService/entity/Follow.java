package CloneProject.InstagramClone.InstagramService.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

@Getter
@Entity
@Table(name = "follows")
@NoArgsConstructor
public class Follow {

    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member toMember;

    @Builder
    public Follow(Member fromMember,Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
    }
}
