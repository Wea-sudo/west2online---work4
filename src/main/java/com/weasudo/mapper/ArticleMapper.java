package com.weasudo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weasudo.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Update("update `article` set `thumb_count`=`thumb_count`+1 where id=#{id} and is_deleted=0")
    int incThumbCount(@Param("id") Long id);
    @Update("update `article` set `thumb_count`=`thumb_count`-1 where id=#{id} and is_deleted=0 and thumb_count>0")
    int decThumbCount(@Param("id") Long id);
    @Update("update `article` set `collect_count`=`collect_count`+1 where id=#{id} and is_deleted=0")
    int incCollectCount(@Param("id") Long id);
    @Update("update `article` set `collect_count`=`collect_count`-1 where id=#{id} and is_deleted=0 and collect_count>0")
    int decCollectCount(@Param("id") Long id);
    @Update("update `article` set `view_count`=`view_count`+1 where id=#{id} and is_deleted=0")
    int incViewCount(@Param("id") Long id);
}
