package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTag;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTagMapping;
import CloneProject.InstagramClone.InstagramService.entity.post.Post;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.exception.post.PostNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.HashTagMappingRepository;
import CloneProject.InstagramClone.InstagramService.repository.HashTagRepository;
import CloneProject.InstagramClone.InstagramService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {
    private final HashTagRepository hashTagRepository;
    private final HashTagMappingRepository hashTagMappingRepository;
    private final PostRepository postRepository;
    private final TokenService tokenService;

    @Override
    public ResponseEntity<ApiResponse> AddHashTagToPost(HashTagDto hashTagDto) {
        if (!tokenService.isTokenValid(hashTagDto.getAccessToken())) {
            throw new JwtIllegalException("유효하지 않은 Json Web Token");
        }

        Post post = postRepository.findById(hashTagDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("없는 게시물입니다."));
        HashTag hashTag = HashTag.builder()
                .tagName(hashTagDto.getHashTag())
                .build();
        HashTagMapping hashTagMapping = HashTagMapping.builder()
                .post(post)
                .hashTag(hashTag)
                .build();

        hashTagRepository.save(hashTag);
        hashTagMappingRepository.save(hashTagMapping);

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Add Hash Tag")
                .data(hashTagDto)
                .build();
    }
}
