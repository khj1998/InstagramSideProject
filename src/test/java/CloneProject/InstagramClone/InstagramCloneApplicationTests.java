package CloneProject.InstagramClone;

import CloneProject.InstagramClone.InstagramService.entity.PostLike;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.repository.LikeRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class InstagramCloneApplicationTests {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	LikeRepository likeRepository;

	@Test
	@Transactional
	void postLikeTest() {
		Member member = new Member();
		Member member2 = new Member();

		member.setEmail("test1@naver.com");
		member.setPassword("12345");
		member.setNickname("김호진");

		member2.setEmail("test2@gmail.com");
		member2.setPassword("12345");
		member2.setNickname("홍길동");

		memberRepository.save(member);
		memberRepository.save(member2);

		Post post = new Post();
		post.setMember(member);
		post.setTitle("김호진의 글");
		post.setContent("글 내용!");
		member.getPostList().add(post);

		postRepository.save(post);

		PostLike postLike1 = new PostLike();
		postLike1.setPost(post);
		postLike1.setMember(member);
		member.getPostLikeList().add(postLike1);
		post.getPostLikeList().add(postLike1);

		PostLike postLike2 = new PostLike();
		postLike2.setPost(post);
		postLike2.setMember(member2);
		member2.getPostLikeList().add(postLike2);
		post.getPostLikeList().add(postLike2);

		likeRepository.save(postLike1);
		likeRepository.save(postLike2);

		Member m = memberRepository.findById(1L).get();
		List<PostLike> postLikeList = m.getPostLikeList();

		for (PostLike postLike : postLikeList) {
			System.out.println("김호진이 좋아요를 누른 글 : "+ postLike.getPost().getTitle());
		}

		Post p = postRepository.findById(1L).get();
		System.out.println("게시글 p가 받은 좋아요 수 : "+p.getPostLikeList().size());
	}
}
