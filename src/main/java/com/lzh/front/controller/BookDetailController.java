package com.lzh.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.common.Result;
import com.lzh.entity.Borrow;
import com.lzh.front.service.BookLendService;
import com.lzh.front.vo.BookLendVo;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-24
 */
@Api(tags = "图书详情页接口")
@RestController
@RequestMapping("/front/bookdetail")
public class BookDetailController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookLendService bookLendService;

    @Autowired
    private BorrowService borrowService;

    @Cacheable("bookdatainfo")
    @ApiOperation("根据图书id获取图书信息")
    @GetMapping("/getById/{id}")
    public Result addOrUpdateAPI_002(@PathVariable String id) {
        return Result.success(bookService.getById(id));
    }

    @ApiOperation("图书借阅接口")
    @PostMapping("/lend")
    public Result bookLend(@RequestBody BookLendVo bookLendVo){
        bookLendService.bookLend(bookLendVo);
        return Result.success();
    }

    @ApiOperation("判断用户是否已经借阅过这本书,并且处于借阅中(0)状态")
    @PostMapping("/judge/lend")
    public Result isLend(@RequestBody BookLendVo bookLendVo){
        return Result.success(borrowService.list(new QueryWrapper<Borrow>()
                .lambda()
                .eq(Borrow::getReaderId, bookLendVo.getReaderId())
                .eq(Borrow::getBookId, bookLendVo.getBookId())
                .ne(Borrow::getState,1)).size());
    }

}
