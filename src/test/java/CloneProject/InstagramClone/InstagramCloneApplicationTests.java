package CloneProject.InstagramClone;

import CloneProject.InstagramClone.InstagramService.entity.Like;
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
import java.util.Optional;

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

		Like like1 = new Like();
		like1.setPost(post);
		like1.setMember(member);
		member.getLikeList().add(like1);
		post.getLikeList().add(like1);

		Like like2 = new Like();
		like2.setPost(post);
		like2.setMember(member2);
		member2.getLikeList().add(like2);
		post.getLikeList().add(like2);

		likeRepository.save(like1);
		likeRepository.save(like2);

		Member m = memberRepository.findById(1L).get();
		List<Like> likeList = m.getLikeList();

		for (Like like : likeList) {
			System.out.println("김호진이 좋아요를 누른 글 : "+like.getPost().getTitle());
		}

		Post p = postRepository.findById(1L).get();
		System.out.println("게시글 p가 받은 좋아요 수 : "+p.getLikeList().size());
	}
}
