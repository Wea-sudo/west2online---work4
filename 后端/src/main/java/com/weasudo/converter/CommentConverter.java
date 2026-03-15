package com.weasudo.converter;

import com.weasudo.dto.CreateCommentDTO;
import com.weasudo.pojo.Comment;
import com.weasudo.vo.CommentVO;

public class CommentConverter {
    public CommentConverter(){}

    public static Comment toEntity(CreateCommentDTO dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setArticleId(dto.getArticleId());
        String parentId = dto.getParentId();
        if(parentId == null || parentId.isBlank()){
            comment.setParentId(null);
        }else{
            comment.setParentId(parentId);
        }

        return comment;
    }
    public static CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setArticleId(comment.getArticleId());
        vo.setParentId(comment.getParentId());
        vo.setUserId(comment.getUserId());
        vo.setCreateTime(comment.getCreateTime());
        vo.setUpdateTime(comment.getUpdateTime());
        return vo;
    }
}
