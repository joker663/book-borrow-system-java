package com.lzh.controller;


import com.lzh.common.Result;
import com.lzh.service.LogService;
import com.lzh.vo.SysLogVo;
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
 * @since 2024-05-01
 */
@Api("登录日志接口")
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    @ApiOperation("查询日志列表")
    @PostMapping("/sysLog/list")
    public Result findPage(@RequestBody SysLogVo sysLogVo) {
        return Result.success(logService.sysLogListAPI_001(sysLogVo.getPageNum(),
                sysLogVo.getPageSize(), sysLogVo.getUsername(), sysLogVo.getDateList()));
    }

}

