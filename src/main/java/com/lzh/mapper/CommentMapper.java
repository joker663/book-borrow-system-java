package com.lzh.mapper;

import com.lzh.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-03
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select c.*,r.nickname,r.avatar_url from t_comment c left join t_reader r on c.reader_id = r.id " +
            "where c.book_id = #{bookId} order by time desc")
    List<Comment> findCommentDetail(@Param("bookId") Integer bookId);

}
