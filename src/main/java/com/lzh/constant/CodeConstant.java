package com.lzh.constant;

import io.swagger.annotations.ApiModel;

/**
 * @Description: 状态编码
 * @Author: lzh
 * @Date: 2024-01-21
 */
@ApiModel("状态编码")
public interface CodeConstant {
    String MSG_SUCCESS = "success";
    String MSG_FAILED = "error";

    /**
     * 200表示成功，其他值表示失败
     */
    String CODE_200 = "200"; //成功

    String CODE_201 = "201"; //失败

    String CODE_401 = "401";  // 权限不足(UNAUTHORIZED)
    String CODE_400 = "400";  // 参数错误
    String CODE_500 = "500"; // 系统错误
    String CODE_600 = "600"; // 其他业务异常

    String DICT_TYPE_ICON = "icon";

    String FILES_KEY = "FILES_FRONT_ALL";

}
