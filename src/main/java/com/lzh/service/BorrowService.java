package com.lzh.service;

import com.lzh.entity.Borrow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.front.vo.BorrowRankingVo;
import com.lzh.vo.BookLendLogQueryVo;
import com.lzh.page.PageData;
import com.lzh.vo.BookLendLogResultVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
public interface BorrowService extends IService<Borrow> {

    PageData<BookLendLogResultVo> listAPI_001(BookLendLogQueryVo bookLendLogVo);

    PageData<BookLendLogResultVo> getOverdue(BookLendLogQueryVo bookLendLogVo);

    void forceBack(Integer id);

    void batchForceBack(List<Integer> ids);

    List<BorrowRankingVo> borrowRanking();

}
