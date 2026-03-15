package com.weasudo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weasudo.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Update("update `article` set `thumb_count`=`thumb_count`+1 where id=#{id} and is_deleted=0")
    int incThumbCount(@Param("id") String id);
    @Update("update `article` set `thumb_count`=`thumb_count`-1 where id=#{id} and is_deleted=0 and thumb_count>0")
    int decThumbCount(@Param("id") String id);
    @Update("update `article` set `collect_count`=`collect_count`+1 where id=#{id} and is_deleted=0")
    int incCollectCount(@Param("id") String id);
    @Update("update `article` set `collect_count`=`collect_count`-1 where id=#{id} and is_deleted=0 and collect_count>0")
    int decCollectCount(@Param("id") String id);
    @Update("update `article` set `view_count`=`view_count`+1 where id=#{id} and is_deleted=0")
    int incViewCount(@Param("id") String id);
    @Update("update `article` set `comment_count`=`comment_count`+1 where id=#{id} and is_deleted=0")
    int incCommentCount(@Param("id") String id);

    void updateArticleData(@Param("articleId") String articleId,
                           @Param("view") int view,
                           @Param("thumb") int thumb,
                           @Param("favor") int favor);
}
