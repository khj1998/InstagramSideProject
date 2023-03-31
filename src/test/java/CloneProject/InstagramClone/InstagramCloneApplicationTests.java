package CloneProject.InstagramClone;

import CloneProject.InstagramClone.InstagramService.entity.Member;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InstagramCloneApplicationTests {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PostRepository postRepository;

	@Test
	void postTest() {
		Member member = new Member();
	}
}
