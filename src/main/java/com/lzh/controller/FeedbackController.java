package com.lzh.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.common.Result;
import com.lzh.entity.*;
import com.lzh.front.vo.BookLendVo;
import com.lzh.page.PageData;
import com.lzh.service.FeedbackService;
import com.lzh.service.ReaderService;
import com.lzh.vo.CommentVo;
import com.lzh.vo.FeedbackResultVo;
import com.lzh.vo.FeedbackResultVo2;
import com.lzh.vo.FeedbackVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-05
 */
@Api("留言反馈接口")
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ReaderService readerService;

    @ApiOperation("查询所有读者留言")
    @GetMapping("/list")
    public Result findData(){
        List<Feedback> feedbackList = feedbackService.list();
        List<FeedbackResultVo> list = new ArrayList<>();
        feedbackList.forEach(feedback -> {
            FeedbackResultVo feedbackResultVo = BeanUtil.copyProperties(feedback, FeedbackResultVo.class);
            list.add(feedbackResultVo);
        });
        return Result.success(list);
    }

    @ApiOperation("保存读者留言")
    @PostMapping("/save")
    public Result saveFeedBack(@RequestBody FeedbackVo feedbackVo){
        Feedback feedback = BeanUtil.copyProperties(feedbackVo, Feedback.class);
        return Result.success(feedbackService.save(feedback));
    }

    @ApiOperation("留言列表")
    @GetMapping("/admin/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam(defaultValue = "") String keywords) {
        // 获取读者Map集合（为了返回值时提高效率）
        Map<Integer, Reader> readerMap = readerService.list().stream()
                .collect(Collectors.toMap(Reader::getId, reader -> reader, (pre, next) -> next));

        LambdaQueryWrapper<Feedback> queryWrapper = new QueryWrapper<Feedback>()
                .lambda()
                .like(StrUtil.isNotBlank(keywords), Feedback::getText, keywords)
                .orderByDesc(Feedback::getCreateTime);

        Page<Feedback> page = new Page<>(pageNum, pageSize);
        feedbackService.page(page,queryWrapper);

        List<FeedbackResultVo2> feedbackResultVo2List = new ArrayList<>();
        if (page.getRecords().size() > 0){
            page.getRecords().forEach(feedback -> {
                FeedbackResultVo2 feedbackResultVo2 = new FeedbackResultVo2();
                feedbackResultVo2.setId(feedback.getId());
                feedbackResultVo2.setCreateTime(feedback.getCreateTime());
                feedbackResultVo2.setText(feedback.getText());
                Optional.ofNullable(readerMap.get(feedback.getReaderId()))
                        .ifPresent(reader -> feedbackResultVo2.setUsername(reader.getUsername()));
                Optional.ofNullable(readerMap.get(feedback.getReaderId()))
                        .ifPresent(reader -> feedbackResultVo2.setNickname(reader.getNickname()));
                feedbackResultVo2List.add(feedbackResultVo2);
            });
        }

        return Result.success(new PageData<FeedbackResultVo2>(feedbackResultVo2List, page.getTotal()));
    }

    @ApiOperation("删除留言")
    @DeleteMapping("/admin/delete/{id}")
    public Result deleteFeedback(@PathVariable Integer id) {
        return Result.success(feedbackService.removeById(id));
    }

    @ApiOperation("批量删除留言")
    @PostMapping("/admin/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(feedbackService.removeByIds(ids));
    }

}

