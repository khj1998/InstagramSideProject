package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;

    @Override
    public void AddPost(Post post) {
        postRepository.save(post);
    }
}
