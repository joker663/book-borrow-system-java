package com.lzh.vo;

import com.lzh.entity.Menu;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-01-22
 */
@Data
@ApiModel("用户登录/注册VO")
public class UserVo {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private String role;
    private List<Menu> menus;

}
