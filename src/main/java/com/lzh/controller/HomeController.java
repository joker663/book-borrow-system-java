package com.lzh.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.common.Result;
import com.lzh.entity.Book;
import com.lzh.entity.Borrow;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import com.lzh.service.ReaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description: 系统首页，用于展示大屏数据
 * @Author: lzh
 * @Date: 2024-01-28
 */
@Api(tags = "系统首页")
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowService borrowService;

    @ApiOperation("获取主页的数量统计信息")
    @GetMapping("/statistics")
    public Result getStatisticsAll() {
        long readerCount = readerService.count();
        List<Book> bookList = bookService.list();
        int totalTotalCount = bookList.stream().mapToInt(Book::getTotalCount).sum();
        int totalLendCount = bookList.stream().mapToInt(Book::getLendCount).sum();
        int totalLeaveCount = bookList.stream().mapToInt(Book::getLeaveCount).sum();

        Map<String, Object> map = new HashMap<>();
        map.put("readerCount",readerCount);
        map.put("totalTotalCount",totalTotalCount);
        map.put("totalLendCount",totalLendCount);
        map.put("totalLeaveCount",totalLeaveCount);
        return Result.success(map);
    }

    @ApiOperation("获取主页的各季度借阅数量统计")
    @GetMapping("/quarter")
    public Result getStatisticsQuarter() {
        List<Borrow> list = borrowService.list();
        int q1 = 0; // 第一季度
        int q2 = 0; // 第二季度
        int q3 = 0; // 第三季度
        int q4 = 0; // 第四季度
        for (Borrow borrow : list) {
            LocalDateTime beginTime = borrow.getBeginTime();
            Quarter quarter = DateUtil.quarterEnum(Date.from(beginTime.atZone(ZoneId.systemDefault()).toInstant()));
            switch (quarter) {
                case Q1: q1 += 1; break;
                case Q2: q2 += 1; break;
                case Q3: q3 += 1; break;
                case Q4: q4 += 1; break;
                default: break;
            }
        }
        return Result.success(CollUtil.newArrayList(q1, q2, q3, q4));
    }

    @ApiOperation("获取主页的各类图书借阅占比")
    @GetMapping("/type")
    public Result getStatisticsType() {
        Map<Integer, Long> collect = borrowService.list().stream()
                .collect(Collectors.groupingBy(Borrow::getBookId, Collectors.counting()));
        Map<Integer, String> stringMap = bookService.list().stream()
                .collect(Collectors.toMap(Book::getId, book -> book.getCategory().split("/")[0], (pre, next) -> next));
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<Integer, Long> integerLongEntry : collect.entrySet()) {
            Integer bookId = integerLongEntry.getKey();
            String category = stringMap.get(bookId).split("/")[0];
            // 查找列表中是否已经有相同name的 Map
            boolean found = false;
            for (Map<String, Object> map : list) {
                if (map.get("name").equals(category)) {
                    // 如果找到了，则更新value
                    Long currentValue = (Long) map.get("value");
                    map.put("value", currentValue + integerLongEntry.getValue());
                    found = true;
                    break;
                }
            }
            // 如果没有找到，则添加一个新的Map
            if (!found) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", category);
                map.put("value", integerLongEntry.getValue());
                list.add(map);
            }
        }
        return Result.success(list);
    }

    @ApiOperation("获取主页的近7天图书借还趋势")
    @GetMapping("/trend")
    public Result getTrendType() {
        // 生成近七天的日期列表
        LocalDate today = LocalDate.now();
        List<String> dateList = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> today.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .collect(Collectors.toList());

        // 初始化结果集，键为日期，值为0
        Map<String, Integer> borrowLendingCounts = new TreeMap<>();
        Map<String, Integer> borrowBackCounts = new TreeMap<>();
        Map<String, Integer> borrowOverdueCounts = new TreeMap<>();
        dateList.forEach(date -> borrowLendingCounts.put(date, 0));
        dateList.forEach(date -> borrowBackCounts.put(date, 0));
        dateList.forEach(date -> borrowOverdueCounts.put(date, 0));

        // 计算近七天的日期范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        // 转换为Date类型，如果你的beginTime字段是Date类型的话
        Date startDate = Date.from(sevenDaysAgo.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        // 创建查询条件
        QueryWrapper<Borrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("begin_time", startDate) // begin_time >= startDate
                .le("begin_time", endDate);  // begin_time <= endDate

        // 查询每天的借阅数量，并更新结果集
        borrowService.list(queryWrapper).forEach(borrow -> {
            Optional.ofNullable(borrow).filter(borrow1 -> borrow1.getState().equals(0)).ifPresent(borrow1 -> {
                String borrowDate = borrow1.getBeginTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // 检查borrowDate是否在dateList中（即近七天内）
                if (dateList.contains(borrowDate)) {
                    // 不用担心日期是否存在问题，因为日期和值都初始化好了
                    borrowLendingCounts.put(borrowDate, borrowLendingCounts.getOrDefault(borrowDate, 0) + 1);
                }
            });
            Optional.ofNullable(borrow).filter(borrow1 -> borrow1.getState().equals(1)).ifPresent(borrow1 -> {
                String borrowDate = borrow1.getBeginTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // 检查borrowDate是否在dateList中（即近七天内）
                if (dateList.contains(borrowDate)) {
                    // 不用担心日期是否存在问题，因为日期和值都初始化好了
                    borrowBackCounts.put(borrowDate, borrowBackCounts.getOrDefault(borrowDate, 0) + 1);
                }
            });
            Optional.ofNullable(borrow).filter(borrow1 -> borrow1.getState().equals(2)).ifPresent(borrow1 -> {
                String borrowDate = borrow1.getBeginTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // 检查borrowDate是否在dateList中（即近七天内）
                if (dateList.contains(borrowDate)) {
                    // 不用担心日期是否存在问题，因为日期和值都初始化好了
                    borrowOverdueCounts.put(borrowDate, borrowOverdueCounts.getOrDefault(borrowDate, 0) + 1);
                }
            });
        });
        Set<String> xAxis = borrowLendingCounts.keySet();
        List<Integer> lendingList = new ArrayList<>(borrowLendingCounts.values());
        List<Integer> backList = new ArrayList<>(borrowBackCounts.values());
        List<Integer> overdueList = new ArrayList<>(borrowOverdueCounts.values());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("xAxis",xAxis);
        resultMap.put("lendingList",lendingList);
        resultMap.put("backList",backList);
        resultMap.put("overdueList",overdueList);
        return Result.success(resultMap);
    }

}
