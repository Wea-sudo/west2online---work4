package com.weasudo.service;

import com.weasudo.pojo.Comment;

public interface CommentService {
    Long createComment(Comment comment);
    Comment getCommentById(Long id);
    boolean updateComment(Comment comment);
    boolean deleteComment(Long id);
}
