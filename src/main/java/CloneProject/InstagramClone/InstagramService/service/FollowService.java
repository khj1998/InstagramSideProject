package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.follow.BlockUserDto;
import CloneProject.InstagramClone.InstagramService.dto.follow.FollowDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface FollowService {
    FollowDto addFollow(FollowDto followDto);
    void unFollow(FollowDto followDto);
    List<FollowDto> getFollowings(HttpServletRequest req);
    List<FollowDto> getFollowers(HttpServletRequest req);
    BlockUserDto blockUser(BlockUserDto blockUserDto);
    void unBlockUser(BlockUserDto blockUserDto);
    List<BlockUserDto> getBlockedUsers(HttpServletRequest req);
    List<BlockUserDto> getBlockingUsers(HttpServletRequest req);
}
