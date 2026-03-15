package com.weasudo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    private String id;
    private String content;
    private String userId;
    private String articleId;
    private String parentId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
