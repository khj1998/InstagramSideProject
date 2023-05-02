package CloneProject.InstagramClone.InstagramService.entity.member;

import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Entity
@Table(name = "blocked_users")
@NoArgsConstructor
public class BlockedMember {

    @Id
    @Column(name = "blocked_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String email;

    @CreationTimestamp
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocking_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private Member toMember;

    @Builder
    public BlockedMember(String email, Member blockingMember, Member blockedMember) {
        this.email = email;
        this.fromMember = blockingMember;
        this.toMember = blockedMember;
    }
}
