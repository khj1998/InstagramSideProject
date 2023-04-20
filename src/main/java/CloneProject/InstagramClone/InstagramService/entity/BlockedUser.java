package CloneProject.InstagramClone.InstagramService.entity;

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
public class BlockedUser {

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
    private Member blockingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private Member blockedMember;

    @Builder
    public BlockedUser(String email,Member blockingMember,Member blockedMember) {
        this.email = email;
        this.blockingMember = blockingMember;
        this.blockedMember = blockedMember;
    }
}
