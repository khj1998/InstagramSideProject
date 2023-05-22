package CloneProject.InstagramClone.InstagramService.service.hashtagservice;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTag;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotAssignedException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.repository.HashTagRepository;
import CloneProject.InstagramClone.InstagramService.service.TokenService;
import CloneProject.InstagramClone.InstagramService.service.hashtagservice.HashTagService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final HashTagRepository hashTagRepository;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetHashTag(HttpServletRequest req,Long hashTagId) {
        String accessToken = tokenService.ExtractTokenFromReq(req);
        tokenService.isTokenValid(accessToken);
        HashTag hashTag = hashTagRepository.findById(hashTagId)
                .orElseThrow(() -> new HashTagNotFoundException("HashTagNotFoundException occurred"));
        HashTagDto hashTagDto = createHashTagDto(hashTag);

        return createGetHashTagResponse(hashTagDto);
    }

    private HashTagDto createHashTagDto(HashTag hashTag) {
        HashTagDto hashTagDto = modelMapper.map(hashTag, HashTagDto.class);
        hashTagDto.setHashTagCount(hashTag.getTagCount());
        return hashTagDto;
    }

    private ResponseEntity<ApiResponse> createGetHashTagResponse(HashTagDto hashTagDto) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get HashTag Count")
                .data(hashTagDto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> GetPopularHashTag() {
        Slice<HashTag> hashTagList = hashTagRepository.findSliceBy(PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"tagCount")));
        if (hashTagList.getSize() == 0) {
            throw new HashTagNotAssignedException("HashTagNotAssignedException occurred");
        }
        List<HashTagDto> hashTagDtoList = getHashTagDtoList(hashTagList.getContent());

        return createPopularHashTagResponse(hashTagDtoList);
    }

    private List<HashTagDto> getHashTagDtoList(List<HashTag> hashTagList) {
        return hashTagList.stream()
                .map(hashTag -> modelMapper.map(hashTag,HashTagDto.class))
                .collect(Collectors.toList());
    }

    private ResponseEntity<ApiResponse> createPopularHashTagResponse(List<HashTagDto> hashTagDtoList) {
        return new ApiResponse.ApiResponseBuilder<>()
                .success(true)
                .message("Get Popular HashTag")
                .data(hashTagDtoList)
                .build();
    }
}
