package com.lzh.common;

import com.lzh.constant.CodeConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 接口统一返回结果
 * @Author: lzh
 * @Date: 2024-01-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "统一返回结果")
public class Result {

    /**
     * 编码：200表示成功，其他值表示失败
     */
    @ApiModelProperty(value = "编码：200表示成功，其他值表示失败")
    private String code;

    /**
     * 是否成功：true成功，false失败
     */
    @ApiModelProperty(value = "是否成功：true成功，false失败")
    private Boolean success;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String msg;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private Object data;

    public static Result success() {
        return new Result(CodeConstant.CODE_200,StatueEnum.STATUE_TRUE.getStatue(), CodeConstant.MSG_SUCCESS, null);
    }

    public static Result success(Object data) {
        return new Result(CodeConstant.CODE_200, StatueEnum.STATUE_TRUE.getStatue(), CodeConstant.MSG_SUCCESS, data);
    }

    public static Result success(Object data,String msg) {
        return new Result(CodeConstant.CODE_200, StatueEnum.STATUE_TRUE.getStatue(), msg, data);
    }

    public static Result error() {
        return new Result(CodeConstant.CODE_201, StatueEnum.STATUE_FALSE.getStatue(), CodeConstant.MSG_FAILED, null);
    }

    public static Result error(String code, String msg) {
        return new Result(code, StatueEnum.STATUE_FALSE.getStatue(), msg, null);
    }

}
