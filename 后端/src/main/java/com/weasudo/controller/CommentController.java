package com.weasudo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.weasudo.common.Response;
import com.weasudo.converter.CommentConverter;
import com.weasudo.dto.CreateCommentDTO;
import com.weasudo.pojo.Comment;
import com.weasudo.service.CommentService;
import com.weasudo.util.JwtUtil;
import com.weasudo.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public Response<Void> createComment(@RequestBody CreateCommentDTO dto, HttpServletRequest request) {
        log.info("创建评论请求: articleId={}, content={}", dto.getArticleId(), dto.getContent());
        String token = request.getHeader("Authorization");
        String userId = JwtUtil.getUserId(token);

        commentService.createComment(dto,userId);

        return Response.success();
    }

    @GetMapping("article/{articleId}")
    public Response<IPage<CommentVO>> getArticleCommentsPage(@PathVariable String articleId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("根据 articleId 获取评论分页请求: articleId={}, page={}, pageSize={}", articleId, page, pageSize);
        IPage<Comment> commentPage = commentService.getCommentsPageByArticleId(articleId, page, pageSize);

        IPage<CommentVO> voPage = commentPage.convert(CommentConverter::toVO);
        return Response.success(voPage);
    }

}
