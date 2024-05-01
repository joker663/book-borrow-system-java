package com.lzh.front.controller;

import cn.hutool.core.util.StrUtil;
import com.lzh.annotation.SysLog;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.front.vo.ReaderVo;
import com.lzh.service.ReaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-24
 */
@RestController
@RequestMapping("/front/auth")
@Api(tags = "前台读者登录/注册接口")
public class FrontLoginController {

    @Autowired
    private ReaderService readerService;

    @SysLog(value = "登录",role = "读者")
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody ReaderVo readerVo) {
        String username = readerVo.getUsername();
        String password = readerVo.getPassword();

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(CodeConstant.CODE_400,"用户名或密码不能为空");
        }
        return Result.success(readerService.login(readerVo));
    }

    @SysLog(value = "注册",role = "读者")
    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody ReaderVo readerVo) {
        String username = readerVo.getUsername();
        String password = readerVo.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(CodeConstant.CODE_400, "用户名或密码不能为空");
        }
        return Result.success(readerService.register(readerVo));
    }

}
