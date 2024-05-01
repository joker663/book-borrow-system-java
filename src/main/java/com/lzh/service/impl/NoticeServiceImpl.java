package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.Notice;
import com.lzh.mapper.NoticeMapper;
import com.lzh.page.PageData;
import com.lzh.service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-07
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public PageData<Notice> noticeListAPI_001(Integer pageNum, Integer pageSize, String keywords) {
        LambdaQueryWrapper<Notice> queryWrapper = new QueryWrapper<Notice>()
                .lambda()
                .like(StrUtil.isNotBlank(keywords), Notice::getContent, keywords)
                .orderByDesc(Notice::getCreateTime);
        Page<Notice> page = new Page<>(pageNum, pageSize);
        noticeMapper.selectPage(page,queryWrapper);
        return new PageData<>(page.getRecords(),page.getTotal());
    }
}
