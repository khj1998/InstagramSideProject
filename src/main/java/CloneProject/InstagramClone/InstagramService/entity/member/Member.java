package CloneProject.InstagramClone.InstagramService.entity.member;

import CloneProject.InstagramClone.InstagramService.entity.comment.Comment;
import CloneProject.InstagramClone.InstagramService.entity.comment.CommentLike;
import CloneProject.InstagramClone.InstagramService.entity.follow.Follow;
import CloneProject.InstagramClone.InstagramService.entity.friend.Friend;
import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import CloneProject.InstagramClone.InstagramService.entity.post.PostLike;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Table(name="members")
@NoArgsConstructor
public class Member implements UserDetails {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<CommentLike> commentLikeList =  new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<PostLike> postLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "fromMember",cascade = CascadeType.ALL)
    private List<Follow> followingList = new ArrayList<>();

    @OneToMany(mappedBy = "toMember",cascade = CascadeType.ALL)
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "fromMember",cascade = CascadeType.ALL)
    private List<BlockedMember> blockingList = new ArrayList<>();

    @OneToMany(mappedBy = "toMember",cascade = CascadeType.ALL)
    private List<BlockedMember> blockedList = new ArrayList<>();

    /**
    자기 자신만 친한 친구 리스트를 조회할 수 있다.
     */
    @OneToMany(mappedBy = "fromMember",cascade = CascadeType.ALL)
    private List<Friend> friendList = new ArrayList<>();

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void AddPost(Post post) {
        post.setMember(this);
        this.getPostList().add(post);
    }

    public void AddPostLike(PostLike postLike) {
        postLike.setMember(this);
        this.getPostLikeList().add(postLike);
    }

    public void AddComment(Comment comment) {
        comment.setMember(this);
        this.getCommentList().add(comment);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
