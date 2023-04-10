package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;

public interface FollowService {
    FollowDto addFollow(FollowDto followDto);
    FollowDto unFollow(FollowDto followDto);
}
