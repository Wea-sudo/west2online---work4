package com.weasudo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.weasudo.dto.CreateArticleDTO;
import com.weasudo.pojo.Article;

import java.util.List;

public interface ArticleService {
        String createArticle(CreateArticleDTO dto, String authorId);
        IPage<Article> getArticlesByAuthorId(String id, Integer page, Integer pageSize);
        Article getArticleById(String id, String userId);
        IPage<Article> getArticlesOrderByTime(Integer page, Integer pageSize);
        IPage<Article> getArticlesOrderByThumb(Integer page, Integer pageSize);
        IPage<Article> searchArticlesOrderByTime(String keyword, Integer page, Integer pageSize);
        IPage<Article> searchArticlesOrderByThumb(String keyword, Integer page, Integer pageSize);
        boolean updateArticle(Article article);
        boolean deleteArticle(String id, String token);

        boolean thumb(String articleId, String userId);
        boolean cancelThumb(String articleId, String userId);
        boolean collect(String articleId, String userId);
        boolean cancelCollect(String articleId, String userId);
        Long getThumbCount(String articleId);
        Long getCollectCount(String articleId);
        void addViewCount(String articleId, String userId);
        Long getViewCount(String articleId);


        boolean hasThumbed(String id, String userId);
        boolean hasCollected(String id, String userId);
}
