package com.weasudo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("article")
public class Article {

    @TableId(value = "id",type = IdType.INPUT)
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
    @TableLogic
    private boolean isDeleted;

}
