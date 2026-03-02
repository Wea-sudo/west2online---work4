package com.weasudo.service;

import com.weasudo.pojo.Article;

public interface ArticleService {
        Long createArticle(Article article);
        Article getArticleByAuthorId(Long id);
        Article getArticleById(Long id);
        boolean updateArticle(Article article);
        boolean deleteArticle(Long id);

        boolean thumb(Long articleId, Long userId);
        boolean cancelThumb(Long articleId, Long userId);
        boolean collect(Long articleId, Long userId);
        boolean cancelCollect(Long articleId, Long userId);



}
