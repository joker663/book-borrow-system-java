package com.lzh.front.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.common.Result;
import com.lzh.entity.Book;
import com.lzh.entity.Collect;
import com.lzh.front.vo.BookLendVo;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.lzh.service.CollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-03
 */
@Api(tags = "我的收藏接口")
@RestController
@RequestMapping("/front/collect")
public class MyCollectController {

    @Autowired
    private CollectService collectService;

    @Autowired
    private BookService bookService;

    @ApiOperation("查询收藏列表")
    @GetMapping("/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam String bookName,
                       @RequestParam Integer readerId) {
        List<Integer> bookIdList = collectService.list(new QueryWrapper<Collect>()
                .lambda()
                .eq(Collect::getReaderId, readerId)
                .eq(Collect::getState,true))
                .stream()
                .map(Collect::getBookId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Book> queryWrapper = new QueryWrapper<Book>()
                .lambda()
                .in(Book::getId, bookIdList)
                .like(StrUtil.isNotBlank(bookName), Book::getName, bookName)
                .orderByDesc(Book::getRecommendScore);

        Page<Book> page = new Page<>(pageNum, pageSize);
        bookService.page(page,queryWrapper);
        return Result.success(new PageData<Book>(page.getRecords(),page.getTotal()));
    }

    @ApiOperation("判断是否收藏")
    @PostMapping("/iscollect")
    public Result getCollectState(@RequestBody BookLendVo bookLendVo) {
        List<Collect> list = collectService.list(new QueryWrapper<Collect>()
                .lambda()
                .eq(Collect::getBookId, bookLendVo.getBookId())
                .eq(Collect::getReaderId, bookLendVo.getReaderId()));
        Collect collect = null;
        if (list.size() > 0){
            collect = list.get(0);
        }else {
            collect = new Collect();
        }
        return Result.success(collect);
    }

    @ApiOperation("收藏图书")
    @PostMapping("/addcollect")
    public Result addCollect(@RequestBody BookLendVo bookLendVo) {
        List<Collect> list = collectService.list(new QueryWrapper<Collect>()
                .lambda()
                .eq(Collect::getBookId, bookLendVo.getBookId())
                .eq(Collect::getReaderId, bookLendVo.getReaderId()));

        Collect collect = null;
        if (list.size() < 1){
            collect = new Collect();
            collect.setBookId(Convert.toInt(bookLendVo.getBookId()));
            collect.setReaderId(bookLendVo.getReaderId());
        }else {
            collect = list.get(0);
        }
        collect.setState(true);
        collectService.saveOrUpdate(collect);
        return Result.success(collect);
    }

    @ApiOperation("取消收藏")
    @PostMapping("/cancelcollect")
    public Result cancelCollect(@RequestBody BookLendVo bookLendVo) {
        List<Collect> list = collectService.list(new QueryWrapper<Collect>()
                .lambda()
                .eq(Collect::getBookId, bookLendVo.getBookId())
                .eq(Collect::getReaderId, bookLendVo.getReaderId()));
        Collect collect = null;
        if (list.size() > 0) {
            collect = list.get(0);
        }
        Objects.requireNonNull(collect).setState(false);
        collectService.updateById(collect);
        return Result.success(collect);
    }

}
