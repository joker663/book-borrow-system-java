package com.lzh.service;

import com.lzh.entity.SysLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.page.PageData;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-05-01
 */
public interface LogService extends IService<SysLog> {

    PageData<SysLog> sysLogListAPI_001(Integer pageNum, Integer pageSize, String username, List<String> dateList);
}
