package com.weasudo.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.weasudo.mapper.ArticleCollectMapper;
import com.weasudo.mapper.ArticleMapper;
import com.weasudo.mapper.ArticleThumbMapper;
import com.weasudo.pojo.Article;
import com.weasudo.pojo.ArticleCollect;
import com.weasudo.pojo.ArticleThumb;
import com.weasudo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ArticleDataSyncTask {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleThumbMapper articleThumbMapper;

    @Autowired
    private ArticleCollectMapper articleCollectMapper;

    // ==================== Redis Key 常量 ====================
    private static final String ARTICLE_THUMB_COUNT_KEY = "article:thumb:count:";
    private static final String ARTICLE_THUMB_USER_KEY = "article:thumb:user:";
    private static final String ARTICLE_THUMB_ARTICLES_KEY = "article:thumb:articles";

    private static final String ARTICLE_COLLECT_COUNT_KEY = "article:collect:count:";
    private static final String ARTICLE_COLLECT_USER_KEY = "article:collect:user:";
    private static final String ARTICLE_COLLECT_ARTICLES_KEY = "article:collect:articles";

    private static final String ARTICLE_VIEW_COUNT_KEY = "article:view:count:";
    private static final String ARTICLE_VIEW_ARTICLES_KEY = "article:view:articles";

    // ==================== 定时任务（每5分钟执行一次） ====================

    /** 同步点赞数据 */
    @Scheduled(fixedRate = 300000)
    public void syncThumbData() {
        log.info("开始同步点赞数据...");
        Set<String> articleIds = redisUtil.members(ARTICLE_THUMB_ARTICLES_KEY);
        if (articleIds == null || articleIds.isEmpty()) {
            return;
        }
        for (String articleId : articleIds) {
            try {
                // 1. 同步点赞计数
                String countKey = ARTICLE_THUMB_COUNT_KEY + articleId;
                Long thumbCount = parse(redisUtil.get(countKey));

                LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Article::getId, articleId)
                        .setSql("thumb_count = thumb_count + ", thumbCount);
                articleMapper.update(null, updateWrapper);

                // 2. 同步点赞用户明细
                String userKey = ARTICLE_THUMB_USER_KEY + articleId;
                Map<String, String> entries = redisUtil.hGetAll(userKey);
                if (entries != null) {
                    for (Map.Entry<String, String> entry : entries.entrySet()) {
                        String userId = entry.getKey();
                        String value = entry.getValue();
                        String[] parts = value.split(":");
                        if (parts.length != 2) continue;
                        String status = parts[0];
                        long timestamp = Long.parseLong(parts[1]);
                        LocalDateTime createTime = Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.systemDefault()).toLocalDateTime();

                        LambdaQueryWrapper<ArticleThumb> deleteWrapper = new LambdaQueryWrapper<>();
                        deleteWrapper.eq(ArticleThumb::getArticleId, articleId)
                                .eq(ArticleThumb::getUserId, userId);

                        if ("1".equals(status)) {
                            // 点赞状态：先删除后插入（保证幂等）
                            articleThumbMapper.delete(deleteWrapper);
                            ArticleThumb thumb = new ArticleThumb();
                            thumb.setArticleId(articleId);
                            thumb.setUserId(userId);
                            thumb.setCreateTime(createTime);
                            articleThumbMapper.insert(thumb);
                        } else if ("-1".equals(status)) {
                            // 取消点赞：删除记录
                            articleThumbMapper.delete(deleteWrapper);
                        }
                    }
                }

                // 3. 删除计数缓存
                redisUtil.delete(countKey);
                // 4. 从 Set 中移除该文章 ID
                redisUtil.removeFromSet(ARTICLE_THUMB_ARTICLES_KEY, articleId);
                log.info("点赞数据同步完成，articleId={}", articleId);
            } catch (Exception e) {
                log.error("同步点赞数据失败，articleId={}", articleId, e);
            }
        }
    }

    /** 同步收藏数据 */
    @Scheduled(fixedRate = 300000)
    public void syncCollectData() {
        log.info("开始同步收藏数据...");
        Set<String> articleIds = redisUtil.members(ARTICLE_COLLECT_ARTICLES_KEY);
        if (articleIds == null || articleIds.isEmpty()) {
            return;
        }
        for (String articleId : articleIds) {
            try {
                // 1. 同步收藏计数
                String countKey = ARTICLE_COLLECT_COUNT_KEY + articleId;
                Long collectCount = parse(redisUtil.get(countKey));

                LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Article::getId, articleId)
                        .setSql("collect_count = collect_count + " + collectCount);
                // 修正：之前误用了 getThumbCount
                articleMapper.update(null, updateWrapper);

                // 2. 同步收藏用户明细
                String userKey = ARTICLE_COLLECT_USER_KEY + articleId;
                Map<String, String> entries = redisUtil.hGetAll(userKey);
                if (entries != null) {
                    for (Map.Entry<String, String> entry : entries.entrySet()) {
                        String userId = entry.getKey();
                        String value = entry.getValue();
                        String[] parts = value.split(":");
                        if (parts.length != 2) continue;
                        String status = parts[0];
                        long timestamp = Long.parseLong(parts[1]);
                        LocalDateTime createTime = Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.systemDefault()).toLocalDateTime();

                        LambdaQueryWrapper<ArticleCollect> deleteWrapper = new LambdaQueryWrapper<>();
                        deleteWrapper.eq(ArticleCollect::getArticleId, articleId)
                                .eq(ArticleCollect::getUserId, userId);

                        if ("1".equals(status)) {
                            // 收藏状态：先删除后插入
                            articleCollectMapper.delete(deleteWrapper);
                            ArticleCollect collect = new ArticleCollect();
                            collect.setArticleId(articleId);
                            collect.setUserId(userId);
                            collect.setCreateTime(createTime);
                            articleCollectMapper.insert(collect);
                        } else if ("-1".equals(status)) {
                            // 取消收藏：删除记录
                            articleCollectMapper.delete(deleteWrapper);
                        }
                    }
                }

                // 3. 删除计数缓存
                redisUtil.delete(countKey);
                // 4. 从 Set 中移除该文章 ID
                redisUtil.removeFromSet(ARTICLE_COLLECT_ARTICLES_KEY, articleId);
                log.info("收藏数据同步完成，articleId={}", articleId);
            } catch (Exception e) {
                log.error("同步收藏数据失败，articleId={}", articleId, e);
            }
        }
    }

    /** 同步浏览数据 */
    @Scheduled(fixedRate = 300000)
    public void syncViewData() {
        log.info("开始同步浏览数据...");
        Set<String> articleIds = redisUtil.members(ARTICLE_VIEW_ARTICLES_KEY);
        if (articleIds == null || articleIds.isEmpty()) {
            return;
        }
        for (String articleId : articleIds) {
            try {
                // 1. 同步浏览计数
                String countKey = ARTICLE_VIEW_COUNT_KEY + articleId;
                Long viewCount = parse(redisUtil.get(countKey));

                LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Article::getId, articleId)
                        .setSql("view_count = view_count + " + viewCount);

                articleMapper.update(null, updateWrapper);

                // 2. 删除计数缓存
                redisUtil.delete(countKey);
                // 3. 从 Set 中移除该文章 ID
                redisUtil.removeFromSet(ARTICLE_VIEW_ARTICLES_KEY, articleId);
                log.info("浏览数据同步完成，articleId={}", articleId);
            } catch (Exception e) {
                log.error("同步浏览数据失败，articleId={}", articleId, e);
            }
        }
    }

    // ==================== 辅助方法 ====================
    private Long parse(String str) {
        if (str == null) return 0L;
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}