package com.weasudo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @NotBlank(message = "文章ID不能为空")
    private String articleId;


    private String parentId = null; // 可选，默认为null，表示一级评论
}
