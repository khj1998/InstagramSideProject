package CloneProject.InstagramClone.InstagramService.entity.friend;

import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Entity
@Table(name = "friends")
@NoArgsConstructor
public class Friend {
    @Id
    @Column(name = "friend_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_friend_fk")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_friend_fk")
    private Member toMember;

    public void setFromMember(Member fromMember) {
        this.fromMember = fromMember;
    }

    public void setToMember(Member toMember) {
        this.toMember = toMember;
    }
}
