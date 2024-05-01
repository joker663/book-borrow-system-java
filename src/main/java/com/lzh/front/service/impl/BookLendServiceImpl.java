package com.lzh.front.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.Book;
import com.lzh.entity.Borrow;
import com.lzh.entity.Reader;
import com.lzh.exception.MyException;
import com.lzh.front.service.BookLendService;
import com.lzh.front.vo.BookLendVo;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import com.lzh.service.ReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-25
 */
@Service
public class BookLendServiceImpl implements BookLendService {

    private static final Logger log = LoggerFactory.getLogger(BookLendServiceImpl.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BorrowService borrowService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bookLend(BookLendVo bookLendVo) {
        log.info("借书流程开始，readerId：{}，bookId：{}",bookLendVo.getReaderId(),bookLendVo.getBookId());
        // 1、判断用户可借书次数是否大于1
        Reader reader = readerService.getById(bookLendVo.getReaderId());
        if (ObjectUtil.isNull(reader)) {
            throw new MyException(CodeConstant.CODE_210,"用户不存在！");
        }
        if (reader.getHaveTimes() < 1) {
            throw new MyException(CodeConstant.CODE_210,"用户借书次数不足！");
        }
        // 2、判断图书数量是否大于0
        Book book = bookService.getById(Convert.toInt(bookLendVo.getBookId()));
        if (ObjectUtil.isNull(book)) {
            throw new MyException(CodeConstant.CODE_210,"图书不存在！");
        }
        if (book.getLeaveCount() < 1) {
            throw new MyException(CodeConstant.CODE_210,"图书数量不足！");
        }
        // 3、向借阅表中插入一条记录
        Borrow borrow = new Borrow();
        borrow.setReaderId(bookLendVo.getReaderId());
        borrow.setBookId(Convert.toInt(bookLendVo.getBookId()));
        LocalDateTime beginTime = LocalDateTime.now();// 借书开始时间
        LocalDateTime endTime = LocalDateTime.now().plusDays(30);// 借书结束时间
        long duringTime = ChronoUnit.DAYS.between(beginTime.toLocalDate(), endTime.toLocalDate());// 借书持续时间
        borrow.setBeginTime(beginTime);
        borrow.setEndTime(endTime);
        borrow.setDuringTime(Convert.toInt(duringTime));
        borrowService.save(borrow);
        // 4、将读者的借书次数减一
        reader.setHaveTimes(reader.getHaveTimes() - 1);
        reader.setUsedTimes(reader.getUsedTimes() + 1);
        readerService.updateById(reader);
        // 5、将图书数量减一
        book.setLeaveCount(book.getLeaveCount() - 1);
        book.setLendCount(book.getLendCount() + 1);
        bookService.updateById(book);

        log.info("借书流程结束：读者：{}，借书：{}，借书时间：{}",bookLendVo.getReaderId(),bookLendVo.getBookId(),beginTime);
    }
}
