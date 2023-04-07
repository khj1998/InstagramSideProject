package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.post.CommentDto;

public interface CommentService {
    CommentDto AddComment(CommentDto commentDto);
    CommentDto EditComment(CommentDto commentDto);
}
