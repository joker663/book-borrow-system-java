package com.lzh.front.service;

import com.lzh.front.vo.BookLendVo;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-25
 */
public interface BookLendService {
    void bookLend(BookLendVo bookLendVo);
}
