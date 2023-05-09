package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.hashtag.HashTagDto;
import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.entity.hashtag.HashTag;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotAssignedException;
import CloneProject.InstagramClone.InstagramService.exception.hashtag.HashTagNotFoundException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.repository.HashTagRepository;
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
        try {
            tokenService.isTokenValid(accessToken);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("JwtExpiredException occurred");
        } catch (Exception e){
            throw new JwtIllegalException("JwtIllegalException occurred");
        }

        Slice<HashTag> hashTagList = hashTagRepository.findSliceBy(PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"tagCount")));
        if (hashTagList.getSize() == 0) {
            throw new HashTagNotAssignedException("HashTagNotAssignedException occurred");
        }
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
