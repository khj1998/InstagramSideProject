package CloneProject.InstagramClone.InstagramService.entity.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Entity
@Table(name = "blocked_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockedMember {

    @Id
    @Column(name = "blocked_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
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
