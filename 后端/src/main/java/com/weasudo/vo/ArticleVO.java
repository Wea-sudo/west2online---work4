package com.weasudo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO {
    private String id;
    private String authorId;
    private String title;
    private String content;      // markdown
    private Long thumbCount;
    private Long collectCount;
    private Long viewCount;
    private Long commentCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean isThumbed;   // 当前用户是否点赞
    private boolean isCollected; // 当前用户是否收藏
}
