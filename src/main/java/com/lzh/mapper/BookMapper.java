package com.lzh.mapper;

import com.lzh.entity.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-12
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

}
