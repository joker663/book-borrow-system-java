package com.lzh.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.Book;
import com.lzh.entity.Borrow;
import com.lzh.entity.Reader;
import com.lzh.front.vo.BorrowRankingVo;
import com.lzh.vo.BookLendLogQueryVo;
import com.lzh.mapper.BorrowMapper;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.service.ReaderService;
import com.lzh.vo.BookLendLogResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements BorrowService {

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowMapper borrowMapper;

    @Override
    public PageData<BookLendLogResultVo> listAPI_001(BookLendLogQueryVo bookLendLogQueryVo) {
        // 获取读者id集合（为了做in查询）
        List<Integer> readerIdList = readerService.list(new QueryWrapper<Reader>()
                        .lambda()
                        .like(StrUtil.isNotBlank(bookLendLogQueryVo.getUsername()),
                                Reader::getUsername, bookLendLogQueryVo.getUsername()))
                .stream()
                .map(Reader::getId)
                .collect(Collectors.toList());

        // 获取读者Map集合（为了返回值时提高效率）
        Map<Integer, Reader> readerMap = readerService.list().stream()
                .collect(Collectors.toMap(Reader::getId, reader -> reader, (pre, next) -> next));

        // 获取图书id集合（为了做in查询）
        List<Integer> bookIdList =  bookService.list(new QueryWrapper<Book>()
                        .lambda()
                        .like(StrUtil.isNotBlank(bookLendLogQueryVo.getBookName()),
                                Book::getName, bookLendLogQueryVo.getBookName()))
                .stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        // 获取图书Map集合（为了返回值时提高效率）
        Map<Integer, Book> bookMap = bookService.list().stream()
                .collect(Collectors.toMap(Book::getId, book -> book, (pre, next) -> next));

        List<LocalDateTime> borrowDateList = parseStr2Date(bookLendLogQueryVo.getBorrowDateList());
        List<LocalDateTime> backDateList = parseStr2Date(bookLendLogQueryVo.getBackDateList());

        QueryWrapper<Borrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(readerIdList.size() > 0,Borrow::getReaderId,readerIdList)
                .in(bookIdList.size() > 0,Borrow::getBookId,bookIdList);
        if (borrowDateList.size() > 0){
            queryWrapper.lambda().between(Borrow::getBeginTime,borrowDateList.get(0),borrowDateList.get(1));
        }
        if (backDateList.size() > 0){
            queryWrapper.lambda().between(Borrow::getEndTime,backDateList.get(0),backDateList.get(1));
        }
        queryWrapper.lambda().orderByDesc(Borrow::getBeginTime);

        Page<Borrow> page = new Page<>(bookLendLogQueryVo.getPageNum(),bookLendLogQueryVo.getPageSize());
        borrowMapper.selectPage(page,queryWrapper);

        // 对返回的数据进行处理
        List<BookLendLogResultVo> bookLendLogResultVoList = new ArrayList<>();
        page.getRecords().forEach(borrow -> {
            BookLendLogResultVo bookLendLogResultVo = new BookLendLogResultVo();

            Reader reader = readerMap.get(borrow.getReaderId());
            bookLendLogResultVo.setReaderNumber(reader.getReaderNumber());
            bookLendLogResultVo.setUsername(reader.getUsername());

            Book book = bookMap.get(borrow.getBookId());
            bookLendLogResultVo.setBookName(book.getName());
            bookLendLogResultVo.setIsbn(book.getIsbn());

            bookLendLogResultVo.setBeginTime(borrow.getBeginTime());
            bookLendLogResultVo.setEndTime(borrow.getEndTime());
            bookLendLogResultVo.setBackTime(borrow.getBackTime());
            long passTime = ChronoUnit.DAYS.between(borrow.getBeginTime().toLocalDate(), LocalDateTime.now().toLocalDate());// 借书时间,已经过去的天数
            bookLendLogResultVo.setLeaveDays(borrow.getDuringTime() - Convert.toInt(passTime));
            bookLendLogResultVo.setState(borrow.getState());

            bookLendLogResultVoList.add(bookLendLogResultVo);
        });
        return new PageData<>(bookLendLogResultVoList,page.getTotal());
    }

    @Override
    public PageData<BookLendLogResultVo> getOverdue(BookLendLogQueryVo bookLendLogQueryVo) {
        // 获取读者id集合（为了做in查询）
        List<Integer> readerIdList = readerService.list(new QueryWrapper<Reader>()
                        .lambda()
                        .like(StrUtil.isNotBlank(bookLendLogQueryVo.getUsername()),
                                Reader::getUsername, bookLendLogQueryVo.getUsername()))
                .stream()
                .map(Reader::getId)
                .collect(Collectors.toList());

        // 获取读者Map集合（为了返回值时提高效率）
        Map<Integer, Reader> readerMap = readerService.list().stream()
                .collect(Collectors.toMap(Reader::getId, reader -> reader, (pre, next) -> next));

        // 获取图书id集合（为了做in查询）
        List<Integer> bookIdList =  bookService.list(new QueryWrapper<Book>()
                        .lambda()
                        .like(StrUtil.isNotBlank(bookLendLogQueryVo.getBookName()),
                                Book::getName, bookLendLogQueryVo.getBookName()))
                .stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        // 获取图书Map集合（为了返回值时提高效率）
        Map<Integer, Book> bookMap = bookService.list().stream()
                .collect(Collectors.toMap(Book::getId, book -> book, (pre, next) -> next));

        QueryWrapper<Borrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(readerIdList.size() > 0,Borrow::getReaderId,readerIdList)
                .in(bookIdList.size() > 0,Borrow::getBookId,bookIdList)
                .eq(Borrow::getState,2)
                .orderByDesc(Borrow::getBeginTime);

        Page<Borrow> page = new Page<>(bookLendLogQueryVo.getPageNum(),bookLendLogQueryVo.getPageSize());
        borrowMapper.selectPage(page,queryWrapper);

        // 对返回的数据进行处理
        List<BookLendLogResultVo> bookLendLogResultVoList = new ArrayList<>();
        page.getRecords().forEach(borrow -> {
            BookLendLogResultVo bookLendLogResultVo = new BookLendLogResultVo();

            Reader reader = readerMap.get(borrow.getReaderId());
            bookLendLogResultVo.setId(borrow.getId());
            bookLendLogResultVo.setReaderNumber(reader.getReaderNumber());
            bookLendLogResultVo.setUsername(reader.getUsername());

            Book book = bookMap.get(borrow.getBookId());
            bookLendLogResultVo.setBookName(book.getName());
            bookLendLogResultVo.setIsbn(book.getIsbn());

            bookLendLogResultVo.setEndTime(borrow.getEndTime());
            bookLendLogResultVo.setState(borrow.getState());

            bookLendLogResultVoList.add(bookLendLogResultVo);
        });
        return new PageData<>(bookLendLogResultVoList,page.getTotal());
    }

    @Transactional
    @Override
    public void forceBack(Integer id) {
        forceBackBook(id);
    }

    @Transactional
    @Override
    public void batchForceBack(List<Integer> ids) {
        for (Integer id : ids) {
            forceBackBook(id);
        }
    }


    @Cacheable("borrowRanking")
    @Override
    public List<BorrowRankingVo> borrowRanking() {
        return borrowMapper.selectBorrowCountGroupByBookId();
    }

    public void forceBackBook(Integer id){
        Borrow borrow = getById(id);
        // 将borrow表中的借阅记录状态改为1(已归还)
        borrow.setState(1);
        borrow.setBackTime(LocalDateTime.now());
        updateById(borrow);
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

    public List<LocalDateTime> parseStr2Date(List<String> strDateList){
        // 创建日期格式化对象，用于将字符串转换为LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<LocalDateTime> localDateTimeList = new ArrayList<>();
        if (ObjectUtil.isNotNull(strDateList) && strDateList.size() > 0){
            LocalDateTime startDate = LocalDate.parse(strDateList.get(0), formatter).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(strDateList.get(1), formatter).atStartOfDay();

            localDateTimeList.add(startDate);
            localDateTimeList.add(endDate);
        }
        return localDateTimeList;
    }
}
