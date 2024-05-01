package com.lzh.front.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.Book;
import com.lzh.entity.Borrow;
import com.lzh.entity.Reader;
import com.lzh.exception.MyException;
import com.lzh.front.vo.MarkScoreVo;
import com.lzh.front.vo.RenewBookVo;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import com.lzh.service.ReaderService;
import com.lzh.utils.TokenUtil;
import com.lzh.vo.BookLendLogResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-01
 */
@Api(tags = "我的借阅接口")
@RestController
@RequestMapping("/front/myborrow")
public class MyBorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderService readerService;

    @Cacheable("borrowing")
    @ApiOperation("借阅中列表")
    @GetMapping("/ing/borrow")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam String bookName,
                       @RequestParam Integer type) {

        // 获取图书id集合（为了做in查询）
        List<Integer> bookIdList =  bookService.list(new QueryWrapper<Book>()
                        .lambda()
                        .like(StrUtil.isNotBlank(bookName),
                                Book::getName, bookName))
                .stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        // 获取图书Map集合（为了返回值时提高效率）
        Map<Integer, Book> bookMap = bookService.list().stream()
                .collect(Collectors.toMap(Book::getId, book -> book, (pre, next) -> next));

        QueryWrapper<Borrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(bookIdList.size() > 0,Borrow::getBookId,bookIdList)
                .eq(bookIdList.size() > 0,Borrow::getState,type)
                .eq(bookIdList.size() == 0,Borrow::getState,3)// 这个只是为了当查询条件没命中时不显示数据
                .eq(Borrow::getReaderId, Objects.requireNonNull(TokenUtil.getCurrentReader()).getId())
                .orderByDesc(Borrow::getBeginTime);

        Page<Borrow> page = new Page<>(pageNum,pageSize);
        borrowService.page(page,queryWrapper);

        // 对返回的数据进行处理
        List<BookLendLogResultVo> bookLendLogResultVoList = new ArrayList<>();
        page.getRecords().forEach(borrow -> {
            BookLendLogResultVo bookLendLogResultVo = new BookLendLogResultVo();
            bookLendLogResultVo.setId(borrow.getId());

            Book book = bookMap.get(borrow.getBookId());
            bookLendLogResultVo.setBookName(book.getName());
            bookLendLogResultVo.setIsbn(book.getIsbn());

            bookLendLogResultVo.setScore(borrow.getScore());
            bookLendLogResultVo.setBeginTime(borrow.getBeginTime());
            bookLendLogResultVo.setEndTime(borrow.getEndTime());
            bookLendLogResultVo.setBackTime(borrow.getBackTime());
            long passTime = ChronoUnit.DAYS.between(borrow.getBeginTime().toLocalDate(), LocalDateTime.now().toLocalDate());// 借书时间,已经过去的天数
            bookLendLogResultVo.setLeaveDays(borrow.getDuringTime() - Convert.toInt(passTime));
            bookLendLogResultVo.setState(borrow.getState());

            bookLendLogResultVoList.add(bookLendLogResultVo);
        });
        return Result.success(new PageData<>(bookLendLogResultVoList,page.getTotal()));
    }

    @ApiOperation("续借图书")
    @PostMapping("/renewBook")
    public Result renewBook(@RequestBody RenewBookVo renewBookVo) {
        Borrow borrow = borrowService.getById(renewBookVo.getId());
        int renewTime = borrow.getDuringTime() - 30 + renewBookVo.getRenewDays();
        if (renewTime > 30){
            throw new MyException(CodeConstant.CODE_210,"您已续借" + (borrow.getDuringTime() - 30)+"天，还可借阅"+(60-borrow.getDuringTime())+"天！");
        }
        // 修改到期日期
        borrow.setEndTime(borrow.getEndTime().plusDays(renewBookVo.getRenewDays()));
        borrow.setDuringTime(borrow.getDuringTime() + renewBookVo.getRenewDays());
        borrowService.updateById(borrow);
        return Result.success();
    }

    @ApiOperation("归还图书")
    @PostMapping("/backBook/{id}")
    public Result backBookByReader(@PathVariable Integer id) {
        backBook(id);
        return Result.success();
    }

    @ApiOperation("用户归还逾期图书")
    @PostMapping("/forceback/{id}")
    public Result forceBack(@PathVariable Integer id) {
        forceBackBook(id);
        return Result.success();
    }

    @ApiOperation("图书评分")
    @PostMapping("/markScore")
    public Result markScore(@RequestBody MarkScoreVo markScoreVo) {
        Borrow borrow = borrowService.getById(markScoreVo.getId());
        borrow.setScore(markScoreVo.getScore());
        borrowService.updateById(borrow);

        // 计算每本书的平均分
        double average = borrowService.list(new QueryWrapper<Borrow>()
                        .lambda()
                        .eq(Borrow::getBookId, borrow.getBookId()))
                .stream()
                .map(Borrow::getScore).mapToDouble(score -> score).summaryStatistics().getAverage();

        Book book = bookService.getById(borrow.getBookId());
        book.setRecommendScore(average);
        bookService.updateById(book);
        return Result.success(borrow);
    }

    public void backBook(Integer id){
        Borrow borrow = borrowService.getById(id);
        // 将borrow表中的借阅记录状态改为1(已归还)
        borrow.setState(1);
        borrow.setBackTime(LocalDateTime.now());
        borrowService.updateById(borrow);
        // 将图书的借阅数量减一，可借阅数量+1
        Book book = bookService.getById(borrow.getBookId());
        book.setLendCount(book.getLendCount() - 1);
        book.setLeaveCount(book.getLeaveCount() + 1);
        bookService.updateById(book);
        // 将读者的可借阅次数加一
        Reader reader = readerService.getById(borrow.getReaderId());
        reader.setHaveTimes(reader.getHaveTimes() + 1);
        reader.setUsedTimes(reader.getUsedTimes() - 1);
        readerService.updateById(reader);
    }

    public void forceBackBook(Integer id){
        Borrow borrow = borrowService.getById(id);
        // 将borrow表中的借阅记录状态改为1(已归还)
        borrow.setState(1);
        borrow.setBackTime(LocalDateTime.now());
        borrowService.updateById(borrow);
        // 将图书的借阅数量减一，可借阅数量+1
        Book book = bookService.getById(borrow.getBookId());
        book.setLendCount(book.getLendCount() - 1);
        book.setLeaveCount(book.getLeaveCount() + 1);
        bookService.updateById(book);
        // 将读者的可借阅次数减一
        Reader reader = readerService.getById(borrow.getReaderId());
        reader.setHaveTimes(reader.getHaveTimes() - 1);
        readerService.updateById(reader);
    }

}
