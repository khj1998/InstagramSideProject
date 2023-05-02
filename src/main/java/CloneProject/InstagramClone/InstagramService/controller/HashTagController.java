package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.dto.response.ApiResponse;
import CloneProject.InstagramClone.InstagramService.service.HashTagService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/hashtags")
@RequiredArgsConstructor
public class HashTagController {

    private final HashTagService hashTagService;
    @GetMapping("/find")
    public ResponseEntity<ApiResponse> getHashTagCounts(HttpServletRequest req,@RequestParam("hashTag") String hashTag) {
        return hashTagService.GetHashTag(req,hashTag);
    }
}
