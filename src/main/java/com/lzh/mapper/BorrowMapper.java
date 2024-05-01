package com.lzh.mapper;

import com.lzh.entity.Borrow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzh.front.vo.BorrowRankingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    @Select("select t1.book_id,t2.name,count(1) as borrowCount from t_borrow t1 left join t_book t2 on t1.book_id=t2.id group by t1.book_id order by borrowCount desc limit 10")
    List<BorrowRankingVo> selectBorrowCountGroupByBookId();

}
