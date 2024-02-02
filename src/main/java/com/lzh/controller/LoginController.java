package com.lzh.controller;

import cn.hutool.core.util.StrUtil;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.service.UserService;
import com.lzh.vo.UserVo;
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
 * @Date: 2024-01-22
 */
@RestController
@RequestMapping("/auth")
@Api(tags = "登录/注册接口")
public class LoginController {

    @Autowired
    private UserService userService;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserVo userVo) {
        String username = userVo.getUsername();
        String password = userVo.getPassword();

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(CodeConstant.CODE_400,"用户名或密码不能为空");
        }
        return Result.success(userService.login(userVo));
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserVo userVo) {
        String username = userVo.getUsername();
        String password = userVo.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(CodeConstant.CODE_400, "用户名或密码不能为空");
        }
        return Result.success(userService.register(userVo));
    }

}
