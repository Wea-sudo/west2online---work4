package com.weasudo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weasudo.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {


}
