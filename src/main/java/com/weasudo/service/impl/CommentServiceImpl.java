package com.weasudo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weasudo.mapper.CommentMapper;
import com.weasudo.pojo.Comment;
import com.weasudo.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public Long createComment(Comment comment) {
        if (this.save(comment)) {
            return comment.getId();
        } else {
            throw new RuntimeException("创建评论失败");
        }
    }

    @Override
    public Comment getCommentById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean updateComment(Comment comment) {
        return this.updateById(comment);
    }

    @Override
    public boolean deleteComment(Long id) {
        return this.removeById(id);
    }
}
