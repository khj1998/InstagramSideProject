package CloneProject.InstagramClone;

import CloneProject.InstagramClone.InstagramService.repository.PostLikeRepository;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class InstagramCloneApplicationTests {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	PostLikeRepository postLikeRepository;

	/**
	 * 게시글 좋아요 테스트
	 */
	@Test
	@Transactional
	void postLikeTest() {
	}
}
