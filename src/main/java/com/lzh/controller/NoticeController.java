package com.lzh.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.common.Result;
import com.lzh.entity.Notice;
import com.lzh.service.NoticeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-07
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("查询公告列表")
    @GetMapping("/list/API_001")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keywords) {
        return Result.success(noticeService.noticeListAPI_001(pageNum, pageSize, keywords));
    }

    /**
     * @param notice
     * @return
     */
    @ApiOperation("新增或编辑公告")
    @PostMapping("/addOrUpdate/API_002")
    public Result addOrUpdateAPI_002(@RequestBody Notice notice) {
        return Result.success(noticeService.saveOrUpdate(notice));
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/delete/API_003/{id}")
    public Result deleteAPI_003(@PathVariable Integer id) {
        return Result.success(noticeService.removeById(id));
    }

    @ApiOperation("批量删除公告")
    @PostMapping("/delete/batch/API_004")
    public Result deleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(noticeService.removeByIds(ids));
    }

    @ApiOperation("启用账号开关")
    @PostMapping("/state/update")
    public Result update(@RequestBody Notice notice){
        noticeService.updateById(notice);
        return Result.success();
    }

    @Cacheable("gglistAll")
    @ApiOperation("查询公告列表")
    @GetMapping("/listAll")
    public Result findAll() {
        return Result.success(noticeService.list(new QueryWrapper<Notice>()
                .lambda()
                .eq(Notice::getState,1)
                .orderByDesc(Notice::getUpdateTime)));
    }

}

