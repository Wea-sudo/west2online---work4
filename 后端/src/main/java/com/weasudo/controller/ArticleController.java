package com.weasudo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.weasudo.common.Response;
import com.weasudo.converter.ArticleConverter;
import com.weasudo.dto.CreateArticleDTO;
import com.weasudo.pojo.Article;
import com.weasudo.service.ArticleService;
import com.weasudo.util.JwtUtil;
import com.weasudo.vo.ArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("/create")
    public Response<String> createArticle(@RequestBody @Valid CreateArticleDTO dto , HttpServletRequest request) {
        //从请求头拿 token
        String token = request.getHeader("Authorization");

        // ️解析 token 得到 userId
        String authorId = JwtUtil.getUserId(token);

        log.info("创建文章请求: title={}, authorId={}", dto.getTitle(), authorId);
        //调用 service
        String articleId = articleService.createArticle(dto, authorId);


        return Response.success(articleId);
    }

    @GetMapping("/{id}")
    public Response<ArticleVO> getArticleById(@PathVariable String id, HttpServletRequest request) {
        log.info("根据 id 获取文章请求: id={}", id);
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);
        Article article = articleService.getArticleById(id,userId);
        ArticleVO vo = ArticleConverter.toVO(article);
        vo.setThumbed(articleService.hasThumbed(id, userId));
        vo.setCollected(articleService.hasCollected(id, userId));
        return Response.success(vo);
    }

    @GetMapping("/author/{authorId}/time")
    public Response<IPage<ArticleVO>> getArticlesByAuthorIdOrderByTime(@PathVariable String authorId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按时间根据 authorId 获取文章请求: authorId={}, page={}, pageSize={}", authorId, page, pageSize);
        IPage<Article> articlePage = articleService.getArticlesByAuthorId(authorId, page, pageSize);
        IPage<ArticleVO> voPage = articlePage.convert(ArticleConverter::toVO);
        return Response.success(voPage);
    }

    @GetMapping("/author/{authorId}/thumb")
    public Response<IPage<ArticleVO>> getArticlesByAuthorIdOrderByThumb(@PathVariable String authorId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按点赞数根据 authorId 获取文章请求: authorId={}, page={}, pageSize={}", authorId, page, pageSize);
        IPage<Article> articlePage = articleService.getArticlesByAuthorId(authorId, page, pageSize);
        IPage<ArticleVO> voPage = articlePage.convert(ArticleConverter::toVO);
        return Response.success(voPage);
    }

    @GetMapping("/search/time")
    public Response<IPage<ArticleVO>> searchArticlesOrderByTime(@RequestParam String keyword, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按时间搜索文章: keyword={}, page={}, pageSize={}", keyword, page, pageSize);
        IPage<Article> articlePage = articleService.searchArticlesOrderByTime(keyword, page, pageSize);
        IPage<ArticleVO> voPage = articlePage.convert(ArticleConverter::toVO);
        return Response.success(voPage);
    }

    @GetMapping("/search/thumb")
    public Response<IPage<ArticleVO>> searchArticlesOrderByThumb(@RequestParam String keyword, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按点赞数搜索文章: keyword={}, page={}, pageSize={}", keyword, page, pageSize);
        IPage<Article> articlePage = articleService.searchArticlesOrderByThumb(keyword, page, pageSize);
        IPage<ArticleVO> voPage = articlePage.convert(ArticleConverter::toVO);
        return Response.success(voPage);
    }

    @GetMapping("/time")
    public Response<IPage<ArticleVO>> getArticlesOrderByTime(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按时间获取文章: page={}, pageSize={}", page, pageSize);
        IPage<Article> articlePage = articleService.getArticlesOrderByTime(page, pageSize);
        IPage<ArticleVO> voPage = articlePage.convert(ArticleConverter::toVO);
        return Response.success(voPage);
    }

    @GetMapping("/thumb")
    public Response<IPage<ArticleVO>> getArticlesOrderByThumb(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按点赞数获取文章: page={}, pageSize={}", page, pageSize);
        IPage<Article> articlePage = articleService.getArticlesOrderByThumb(page, pageSize);
        IPage<ArticleVO> voPage = articlePage.convert(ArticleConverter::toVO);
        return Response.success(voPage);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteArticleById(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info("删除文章请求: id={}", id);
        articleService.deleteArticle(id,token);
        return Response.success();
    }

    @PostMapping("/{id}/thumb")
    public Response<Void> thumb(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);
        articleService.thumb(id, userId);
        return Response.success();
    }

    @DeleteMapping("/{id}/thumb")
    public Response<Void> cancelThumb(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);
        articleService.cancelThumb(id, userId);
        return Response.success();
    }

    @PostMapping("/{id}/collect")
    public Response<Void> collect(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);
        articleService.collect(id, userId);
        return Response.success();
    }

    @DeleteMapping("/{id}/collect")
    public Response<Void> cancelCollect(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);
        articleService.cancelCollect(id, userId);
        return Response.success();
    }

}
