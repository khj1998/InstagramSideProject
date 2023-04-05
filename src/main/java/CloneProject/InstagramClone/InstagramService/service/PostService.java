package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.CommentDto;
import CloneProject.InstagramClone.InstagramService.dto.PostDto;

public interface PostService {
    PostDto AddPost(PostDto postDto);
    CommentDto AddComment(CommentDto commentDto);
}
