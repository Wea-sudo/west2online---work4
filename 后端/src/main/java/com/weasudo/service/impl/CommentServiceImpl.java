package com.weasudo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weasudo.exception.BusinessException;
import com.weasudo.converter.CommentConverter;
import com.weasudo.dto.CreateCommentDTO;
import com.weasudo.mapper.ArticleMapper;
import com.weasudo.mapper.CommentMapper;
import com.weasudo.pojo.Article;
import com.weasudo.pojo.Comment;
import com.weasudo.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.weasudo.common.ResponseCodeEnum.*;


@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public void createComment(CreateCommentDTO dto, String userId) {
        log.info("创建评论请求: articleId={}, userId={}", dto.getArticleId(), userId);
        Comment comment = CommentConverter.toEntity(dto);
        comment.setUserId(userId);
        comment.setId(IdWorker.getIdStr());
        Article article = articleMapper.selectById(comment.getArticleId());

        if (article == null) {
            log.warn("评论创建失败，文章不存在: articleId={}, userId={}", comment.getArticleId(), comment.getUserId());
            throw new BusinessException(ARTICLE_NOT_EXIST);
        }
        articleMapper.incCommentCount(article.getId());
        if (!this.save(comment)) {
            log.warn("评论创建失败: articleId={}, userId={}", comment.getArticleId(), comment.getUserId());
            throw new BusinessException(CREATE_COMMENT_FAILED);
        }
        log.info("评论创建成功: articleId={}, userId={}", comment.getArticleId(), comment.getUserId());

    }

    @Override
    public Comment getCommentById(String id) {
        return this.getById(id);
    }

    @Override
    public IPage<Comment> getCommentsPageByArticleId(String articleId, Integer page, Integer pageSize) {
        IPage<Comment> commentPage = new Page<>(page, pageSize);
        return this.lambdaQuery()
                .eq(Comment::getArticleId, articleId)
                .page(commentPage);
    }

    @Override
    public boolean updateComment(Comment comment) {
        return this.updateById(comment);
    }

    @Override
    public boolean deleteComment(String id) {
        return this.removeById(id);
    }
}
