package CloneProject.InstagramClone.InstagramService.entity;

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


}
