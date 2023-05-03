package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTag;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotFoundException;
import CloneProject.InstagramClone.InstagramService.repository.HashTagRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final HashTagRepository hashTagRepository;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetPopularHashTag(HttpServletRequest req) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        tokenService.isTokenValid(accessToken);
        Slice<HashTag> hashTagList = hashTagRepository.findSliceBy(PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"tagCount")));
        List<HashTagDto> resDtoList = new ArrayList<>();

        List<HashTag> hashTags = hashTagList.getContent();

        for (HashTag hashTag : hashTags) {
            resDtoList.add(modelMapper.map(hashTag, HashTagDto.class));
        }

        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get Popular HashTag")
                .data(resDtoList)
                .build();
    }
}
