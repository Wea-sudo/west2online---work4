package com.weasudo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weasudo.mapper.ArticleCollectMapper;
import com.weasudo.mapper.ArticleMapper;
import com.weasudo.mapper.ArticleThumbMapper;
import com.weasudo.pojo.Article;
import com.weasudo.pojo.ArticleCollect;
import com.weasudo.pojo.ArticleThumb;
import com.weasudo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleThumbMapper articleThumbMapper;

    @Autowired
    private ArticleCollectMapper articleCollectMapper;



    @Override
    public Long createArticle(Article article) {
        if(this.save(article)) {
            return article.getId();
        } else {
            throw new RuntimeException("创建文章失败");
        }

    }

    @Override
    public Article getArticleByAuthorId(Long id) {
        return this.lambdaQuery()
                .eq(Article::getAuthorId, id)
                .one();
    }

    @Override
    public Article getArticleById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean updateArticle(Article article) {
        return this.updateById(article);
    }

    @Override
    public boolean deleteArticle(Long id) {
        return this.removeById(id);
    }

    @Transactional
    @Override
    public boolean thumb(Long articleId, Long userId) {

        // 1. 判断是否存在点赞记录
        LambdaQueryWrapper<ArticleThumb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleThumb::getArticleId, articleId)
                .eq(ArticleThumb::getUserId, userId);

        if (articleThumbMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已点赞");
        }

        // 2. 插入article_thumb表
        ArticleThumb thumb = new ArticleThumb();
        thumb.setArticleId(articleId);
        thumb.setUserId(userId);
        articleThumbMapper.insert(thumb);

        // 3. 更新文章点赞数
        return this.lambdaUpdate()
                        .eq(Article::getId, articleId)
                        .setSql("thumb_count = thumb_count + 1")
                        .update();
    }


    @Transactional
    @Override
    public boolean cancelThumb(Long articleId, Long userId) {
        // 1. 删除article_thumb表里数据
        LambdaQueryWrapper<ArticleThumb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleThumb::getArticleId, articleId)

                .eq(ArticleThumb::getUserId, userId);
        int row = articleThumbMapper.delete(wrapper);

        // 2.若数据为点赞记录，则删除成功，row为1；若数据不存在，则删除失败，row为0
        if (row == 0) {
            throw new RuntimeException("未点赞");
        }


        // 3. 更新文章点赞数
        return this.lambdaUpdate()
                .eq(Article::getId, articleId)
                .gt(Article::getThumbCount, 0)
                .setSql("thumb_count = thumb_count - 1")
                .update();
    }

    @Transactional
    @Override
    public boolean collect(Long articleId, Long userId) {
        // 1. 判断是否存在收藏记录
        LambdaQueryWrapper<ArticleCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCollect::getArticleId, articleId)
                .eq(ArticleCollect::getUserId, userId);

        if (articleCollectMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已收藏");
        }

        // 2. 插入article_collect表
        ArticleCollect collect = new ArticleCollect();
        collect.setArticleId(articleId);
        collect.setUserId(userId);
        articleCollectMapper.insert(collect);

        // 3. 更新文章收藏数
        return this.lambdaUpdate()
                .eq(Article::getId, articleId)
                .setSql("collect_count = collect_count + 1")
                .update();
    }

    @Transactional
    @Override
    public boolean cancelCollect(Long articleId, Long userId) {
        // 1. 删除article_collect表里数据
        LambdaQueryWrapper<ArticleCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCollect::getArticleId, articleId)
                .eq(ArticleCollect::getUserId, userId);
        int row = articleCollectMapper.delete(wrapper);

        // 2.若数据为收藏记录，则删除成功，row为1；若数据不存在，则删除失败，row为0
        if (row == 0) {
            throw new RuntimeException("未收藏");
        }

        // 3. 更新文章收藏数
        return this.lambdaUpdate()
                .eq(Article::getId, articleId)
                .gt(Article::getCollectCount, 0)
                .setSql("collect_count = collect_count - 1")
                .update();
    }

}
