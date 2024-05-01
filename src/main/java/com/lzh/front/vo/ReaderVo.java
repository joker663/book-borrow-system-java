package com.lzh.front.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-24
 */
@Data
@ApiModel("读者登录/注册VO")
public class ReaderVo {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
}
