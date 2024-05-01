package com.lzh.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.SysLog;
import com.lzh.entity.User;
import com.lzh.mapper.LogMapper;
import com.lzh.page.PageData;
import com.lzh.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-05-01
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, SysLog> implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public PageData<SysLog> sysLogListAPI_001(Integer pageNum, Integer pageSize, String username, List<String> dateList) {
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
        List<LocalDateTime> logDateList = parseStr2Date(dateList);
        queryWrapper.lambda()
                .like(StrUtil.isNotBlank(username),SysLog::getUsername,username)
                .orderByDesc(SysLog::getTime);
        if (logDateList.size() > 0){
            queryWrapper.lambda().between(SysLog::getTime,logDateList.get(0),logDateList.get(1));
        }
        Page<SysLog> page = new Page<>(pageNum,pageSize);
        logMapper.selectPage(page,queryWrapper);
        return new PageData<>(page.getRecords(),page.getTotal());
    }

    public List<LocalDateTime> parseStr2Date(List<String> strDateList){
        // 创建日期格式化对象，用于将字符串转换为LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<LocalDateTime> localDateTimeList = new ArrayList<>();
        if (ObjectUtil.isNotNull(strDateList) && strDateList.size() > 0){
            LocalDateTime startDate = LocalDateTime.parse(strDateList.get(0), formatter);
            LocalDateTime endDate = LocalDateTime.parse(strDateList.get(1), formatter);

            localDateTimeList.add(startDate);
            localDateTimeList.add(endDate);
        }
        return localDateTimeList;
    }
}
