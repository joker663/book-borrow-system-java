package com.lzh.vo;

import lombok.Data;

/**
 * @Description: 账号密码Vo
 * @Author: lzh
 * @Date: 2024-02-02
 */
@Data
public class PasswordVo {
    private String username;
    private String phone;// 用来找回密码
    private String password;
    private String newPassword;
}
