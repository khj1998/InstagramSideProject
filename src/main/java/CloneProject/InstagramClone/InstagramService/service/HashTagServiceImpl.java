package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTag;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.HashTagRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.asm.IModelFilter;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final HashTagRepository hashTagRepository;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<ApiResponse> GetHashTag(HttpServletRequest req,String hashTagName) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        tokenService.isTokenValid(accessToken);
        HashTag hashTag = hashTagRepository.findByTagName("#"+hashTagName);

        if (hashTag == null) {
            throw new HashTagNotFoundException("HashTagNotFoundException occurred");
        }

        HashTagDto resDto = modelMapper.map(hashTag, HashTagDto.class);
        resDto.setHashTagCount(hashTag.getTagCount());

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get HashTag Count")
                .data(resDto)
                .build();
    }
}
