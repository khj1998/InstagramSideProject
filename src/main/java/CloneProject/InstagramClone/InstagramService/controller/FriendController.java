package CloneProject.InstagramClone.InstagramService.controller;

import CloneProject.InstagramClone.InstagramService.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
}
