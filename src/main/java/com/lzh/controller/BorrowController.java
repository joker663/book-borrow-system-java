package com.lzh.controller;


import com.lzh.common.Result;
import com.lzh.service.BorrowService;
import com.lzh.vo.BookLendLogQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Api("借阅管理接口")
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @ApiOperation("获取借阅日志列表")
    @PostMapping("/list")
    public Result list(@RequestBody BookLendLogQueryVo bookLendLogVo) {
        return Result.success(borrowService.listAPI_001(bookLendLogVo));
    }

    @ApiOperation("获取逾期的借阅记录")
    @PostMapping("/overdue")
    public Result getOverdue(@RequestBody BookLendLogQueryVo bookLendLogVo) {
        return Result.success(borrowService.getOverdue(bookLendLogVo));
    }

    @ApiOperation("强制归还")
    @PostMapping("/forceback/{id}")
    public Result forceBack(@PathVariable Integer id) {
        borrowService.forceBack(id);
        return Result.success();
    }

    @ApiOperation("批量强制归还")
    @PostMapping("/batch/forceback")
    public Result batchForceBack(@RequestBody List<Integer> ids) {
        borrowService.batchForceBack(ids);
        return Result.success();
    }

}

