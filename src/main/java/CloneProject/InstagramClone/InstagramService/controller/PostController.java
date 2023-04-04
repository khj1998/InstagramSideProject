package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.Post;
import CloneProject.InstagramClone.InstagramService.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPost(@RequestBody Post post) {
        postService.AddPost(post);
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Post")
                .data(post)
                .build();
    }
}
