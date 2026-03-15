package com.weasudo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weasudo.exception.BusinessException;
import com.weasudo.converter.ArticleConverter;
import com.weasudo.dto.CreateArticleDTO;
import com.weasudo.mapper.ArticleCollectMapper;
import com.weasudo.mapper.ArticleMapper;
import com.weasudo.mapper.ArticleThumbMapper;
import com.weasudo.mapper.CommentMapper;
import com.weasudo.pojo.Article;
import com.weasudo.pojo.ArticleCollect;
import com.weasudo.pojo.ArticleThumb;
import com.weasudo.pojo.Comment;
import com.weasudo.service.ArticleService;
import com.weasudo.util.JwtUtil;
import com.weasudo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.weasudo.common.ResponseCodeEnum.*;


@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleThumbMapper articleThumbMapper;

    @Autowired
    private ArticleCollectMapper articleCollectMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RedisUtil redisUtil;


    // 点赞相关（已存在，略）
    private static final String ARTICLE_THUMB_COUNT_KEY = "article:thumb:count:";
    private static final String ARTICLE_THUMB_USER_KEY = "article:thumb:user:";
    private static final String ARTICLE_THUMB_ARTICLES_KEY = "article:thumb:articles";   // 已有

    // 收藏相关
    private static final String ARTICLE_COLLECT_COUNT_KEY = "article:collect:count:";
    private static final String ARTICLE_COLLECT_USER_KEY = "article:collect:user:";
    private static final String ARTICLE_COLLECT_ARTICLES_KEY = "article:collect:articles";

    // 浏览相关
    private static final String ARTICLE_VIEW_COUNT_KEY = "article:view:count:";
    private static final String ARTICLE_VIEW_LIMIT_KEY = "article:view:user:";
    private static final String ARTICLE_VIEW_ARTICLES_KEY = "article:view:articles";



    @Override
    public String  createArticle(CreateArticleDTO dto, String authorId) {

        Article article = ArticleConverter.toEntity((dto));
        article.setId(IdWorker.getIdStr());
        article.setAuthorId(authorId);
        if(!this.save(article)){
            log.warn("文章创建失败");
            throw new BusinessException(CREATE_ARTICLE_FAILED);
        }

        log.info("创建文章成功: title={}, authorId={}", article.getTitle(), authorId);
        return article.getId();
    }

    @Override
    public IPage<Article> getArticlesByAuthorId(String id, Integer page, Integer pageSize) {
        IPage<Article> pageParam = new Page<>(page, pageSize);

        log.info("根据 authorId 获取文章请求: authorId={}, page={}, pageSize={}", id, page, pageSize);
        return this.lambdaQuery()
                .eq(Article::getAuthorId, id)
                .page(pageParam);
    }

    @Override
    public IPage<Article> getArticlesOrderByTime(Integer page, Integer pageSize) {
        Page<Article> pageParam = new Page<>(page, pageSize);

        return this.lambdaQuery()
                .orderByDesc(Article::getCreateTime)
                // 按照创建时间分组，最新的文章会在前面
                .page(pageParam);
    }

    @Override
    public IPage<Article> getArticlesOrderByThumb(Integer page, Integer pageSize) {
        Page<Article> pageParam = new Page<>(page, pageSize);

        return this.lambdaQuery()
                .orderByDesc(Article::getCreateTime)
                .page(pageParam);
    }

    @Override
    public IPage<Article> searchArticlesOrderByTime(String keyword, Integer page, Integer pageSize) {
        Page<Article> pageParam = new Page<>(page, pageSize);

        return this.lambdaQuery()
                .like(Article::getTitle, keyword)
                .or()
                .like(Article::getContent, keyword)
                .orderByDesc(Article::getCreateTime)
                .page(pageParam);
    }

    @Override
    public IPage<Article> searchArticlesOrderByThumb(String keyword, Integer page, Integer pageSize) {
        Page<Article> pageParam = new Page<>(page, pageSize);

        return this.lambdaQuery()
                .like(Article::getTitle, keyword)
                .or()
                .like(Article::getContent, keyword)
                .orderByAsc(Article::getThumbCount)
                .page(pageParam);
    }


    public Article getArticleById(String id,String userId) {
        Article article = this.getById(id);
        article.setThumbCount(this.getThumbCount(id));
        article.setCollectCount(this.getCollectCount(id));
        article.setViewCount(this.getViewCount(id));
        if(article == null) {
            log.warn("文章不存在: id={}", id);
            throw new BusinessException(ARTICLE_NOT_EXIST);
        }
        addViewCount(id, userId);

        log.info("获取文章成功: id={}", id);
        return article;
    }

    @Override
    public boolean updateArticle(Article article) {
        return this.updateById(article);
    }

    @Transactional
    @Override
    public boolean deleteArticle(String id, String token) {
        Article article = this.getById(id);
        if(article == null) {
            log.warn("文章不存在: id={}", id);
            throw new BusinessException(ARTICLE_NOT_EXIST);
        }

        String userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);
        boolean isAuthor = article.getAuthorId().equals(userId);
        boolean isAdmin = "ADMIN".equals(role);
        if(!isAuthor && !isAdmin) {
            log.warn("无权删除文章: id={}, userId={}", id, userId);
            throw new BusinessException(NO_AUTH);
        }

        // 1 删除点赞
        articleThumbMapper.delete(
                new LambdaQueryWrapper<ArticleThumb>()
                        .eq(ArticleThumb::getArticleId, article.getId())
        );

        // 2 删除收藏
        articleCollectMapper.delete(
                new LambdaQueryWrapper<ArticleCollect>()
                        .eq(ArticleCollect::getArticleId, article.getId())
        );

        // 3 删除评论
        commentMapper.delete(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, article.getId())
        );
        log.info("删除文章成功: id={}", id);
        return this.removeById(id);
    }

    @Transactional
    @Override
    public boolean thumb(String articleId, String userId) {

        log.info("点赞请求: articleId={}, userId={}", articleId, userId);

        String userKey = ARTICLE_THUMB_USER_KEY + articleId;
        String countKey = ARTICLE_THUMB_COUNT_KEY + articleId;

        // 1 查询用户状态
        Object value = redisUtil.hGet(userKey, userId);

        if (value != null) {

            String status = value.toString().split(":")[0];

            if ("1".equals(status)) {
                throw new BusinessException(ALREADY_THUMB);
            }
        }

        // 2 设置点赞状态
        String statusValue = "1:" + System.currentTimeMillis();

        redisUtil.hSet(userKey, userId, statusValue);

        // 3 点赞数+1
        redisUtil.increment(countKey, 1);

        // 4 记录被点赞的文章
        redisUtil.addToSet(ARTICLE_THUMB_ARTICLES_KEY, articleId, 7, TimeUnit.DAYS);

        log.info("点赞成功: articleId={}, userId={}", articleId, userId);
        return true;
    }


    @Transactional
    @Override
    public boolean cancelThumb(String articleId, String userId) {

        log.info("取消点赞请求: articleId={}, userId={}", articleId, userId);
        String userKey = ARTICLE_THUMB_USER_KEY + articleId;
        String countKey = ARTICLE_THUMB_COUNT_KEY + articleId;

        Object value = redisUtil.hGet(userKey, userId);

        if (value == null) {
            throw new BusinessException(NOT_THUMB);
        }

        String statusValue = "-1:" + System.currentTimeMillis();

        // 标记取消点赞
        redisUtil.hSet(userKey, userId, statusValue);

        // 点赞数-1
        redisUtil.decrement(countKey,1);

        // 记录文章变化
        redisUtil.addToSet(ARTICLE_THUMB_ARTICLES_KEY, articleId, 7, TimeUnit.DAYS);

        log.info("取消点赞成功: articleId={}, userId={}", articleId, userId);
        return true;
    }

    @Transactional
    @Override
    public boolean collect(String articleId, String userId) {
        log.info("收藏请求: articleId={}, userId={}", articleId, userId);

        String userKey = ARTICLE_COLLECT_USER_KEY + articleId;
        String countKey = ARTICLE_COLLECT_COUNT_KEY + articleId;

        // 1 查询用户状态
        Object value = redisUtil.hGet(userKey, userId);

        if (value != null) {

            String status = value.toString().split(":")[0];

            if ("1".equals(status)) {
                throw new BusinessException(ALREADY_COLLECT);
            }
        }

        // 2 设置收藏状态
        String statusValue = "1:" + System.currentTimeMillis();

        redisUtil.hSet(userKey, userId, statusValue);

        // 3 收藏数+1
        redisUtil.increment(countKey, 1);

        // 4 记录被收藏的文章
        redisUtil.addToSet(ARTICLE_COLLECT_ARTICLES_KEY, articleId, 7, TimeUnit.DAYS);

        log.info("收藏成功: articleId={}, userId={}", articleId, userId);
        return true;
    }

    @Transactional
    @Override
    public boolean cancelCollect(String articleId, String userId) {
        log.info("取消收藏请求: articleId={}, userId={}", articleId, userId);
        String userKey = ARTICLE_COLLECT_USER_KEY + articleId;
        String countKey = ARTICLE_COLLECT_COUNT_KEY + articleId;

        Object value = redisUtil.hGet(userKey, userId);

        if (value == null) {
            throw new BusinessException(NOT_COLLECT);
        }

        String statusValue = "-1:" + System.currentTimeMillis();

        // 标记取消收藏
        redisUtil.hSet(userKey, userId, statusValue);

        // 收藏数-1
        redisUtil.decrement(countKey,1);

        // 记录文章变化
        redisUtil.addToSet(ARTICLE_COLLECT_ARTICLES_KEY, articleId, 7, TimeUnit.DAYS);

        log.info("取消收藏成功: articleId={}, userId={}", articleId, userId);
        return true;
    }


    @Override
    public Long getThumbCount(String articleId) {

        Article article = this.getById(articleId);

        Long dbCount = article.getThumbCount();

        String key = ARTICLE_THUMB_COUNT_KEY + articleId;

        Long redisCount = redisUtil.getCount(key);

        return dbCount + redisCount;
    }

    @Override
    public Long getCollectCount(String articleId) {

        Article article = this.getById(articleId);

        Long dbCount = article.getCollectCount();

        String key = ARTICLE_COLLECT_COUNT_KEY + articleId;

        Long redisCount = redisUtil.getCount(key);

        return dbCount + redisCount;
    }


    public void addViewCount(String articleId, String userId) {
        String limitKey = ARTICLE_VIEW_LIMIT_KEY + articleId + ":" + userId;
        String countKey = ARTICLE_VIEW_COUNT_KEY + articleId;

        // 1 判断是否已经浏览
        Object hasViewed = redisUtil.get(limitKey);

        if (hasViewed == null) {

            // 2 浏览数 +1
            redisUtil.increment(countKey, 1);

            // 3 记录浏览的文章
            redisUtil.addToSet(ARTICLE_VIEW_ARTICLES_KEY, articleId, 1, TimeUnit.DAYS);

            // 4 设置浏览限制（1小时）
            redisUtil.set(limitKey, "1", 1, TimeUnit.HOURS);
            log.info("浏览数增加: articleId={}, userId={}", articleId, userId);

        }

    }


    @Override
    public Long getViewCount(String articleId) {

        Article article = this.getById(articleId);

        Long dbCount = article.getViewCount();

        String key = ARTICLE_VIEW_COUNT_KEY + articleId;

        Long redisCount = redisUtil.getCount(key);

        return dbCount + redisCount;
    }

    @Override
    public boolean hasThumbed(String articleId, String userId) {
        String userKey = ARTICLE_THUMB_USER_KEY + articleId;
        Object value = redisUtil.hGet(userKey, userId);

        if (value != null) {
            String status = value.toString().split(":")[0];
            return "1".equals(status);
        }

        // 缓存里没有，再查数据库
        ArticleThumb thumb = articleThumbMapper.selectOne(
                new LambdaQueryWrapper<ArticleThumb>()
                        .eq(ArticleThumb::getArticleId, articleId)
                        .eq(ArticleThumb::getUserId, userId)
        );

        return thumb != null;
    }

    @Override
    public boolean hasCollected(String articleId, String userId) {
        String userKey = ARTICLE_COLLECT_USER_KEY + articleId;
        Object value = redisUtil.hGet(userKey, userId);

        if (value != null) {
            String status = value.toString().split(":")[0];
            return "1".equals(status);
        }

        // 缓存里没有，再查数据库
        ArticleCollect collect = articleCollectMapper.selectOne(
                new LambdaQueryWrapper<ArticleCollect>()
                        .eq(ArticleCollect::getArticleId, articleId)
                        .eq(ArticleCollect::getUserId, userId)
        );

        return collect != null;
    }


}
