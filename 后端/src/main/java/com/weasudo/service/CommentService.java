package com.weasudo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.weasudo.dto.CreateCommentDTO;
import com.weasudo.pojo.Comment;

public interface CommentService {
    void createComment(CreateCommentDTO dto, String userId);
    Comment getCommentById(String id);
    public IPage<Comment> getCommentsPageByArticleId(String articleId, Integer page, Integer pageSize);
    boolean updateComment(Comment comment);
    boolean deleteComment(String id);
}
