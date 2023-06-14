package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.response.RestApiResponse;
import CloneProject.InstagramClone.InstagramService.service.hashtagservice.HashTagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/hashtags")
@RequiredArgsConstructor
public class HashTagController {
    private final HashTagService hashTagService;

    @ApiOperation("해시태그 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "해시태그 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "해시태그 조회 실패")
    })
    @GetMapping("/find")
    public ResponseEntity<RestApiResponse> getHashTag(HttpServletRequest req,
                                                      @RequestParam("id") Long id) {
        return hashTagService.GetHashTag(req,id);
    }

    @ApiOperation("인기 해시태그 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "인기 해시태그 조회 성공"),
            @ApiResponse(responseCode = "403",description = "맴버 권한이 아님"),
            @ApiResponse(responseCode = "404",description = "인기 해시태그 조회 실패")
    })
    @GetMapping("/popular-tags")
    public ResponseEntity<RestApiResponse> getPopularTags() {
        return hashTagService.GetPopularHashTag();
    }
}
