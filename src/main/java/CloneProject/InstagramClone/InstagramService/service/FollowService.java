package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.FollowerDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowingDto;

public interface FollowService {
    FollowerDto addFollower(FollowerDto followerDto);
    FollowerDto addFollowing(FollowingDto followerDto);
}
