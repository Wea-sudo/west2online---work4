package com.weasudo.converter;

import com.weasudo.dto.CreateArticleDTO;
import com.weasudo.pojo.Article;
import com.weasudo.vo.ArticleVO;

public class ArticleConverter {
    public ArticleConverter() {
    }

    public static Article toEntity(CreateArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        return article;
    }

    public static ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setContent(article.getContent());
        vo.setAuthorId(article.getAuthorId());
        vo.setViewCount(article.getViewCount());
        vo.setThumbCount(article.getThumbCount());
        vo.setCommentCount(article.getCommentCount());
        vo.setCollectCount(article.getCollectCount());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        return vo;
    }

}

