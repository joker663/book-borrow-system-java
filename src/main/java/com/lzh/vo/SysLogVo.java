package com.lzh.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-25
 */
@Data
@ApiModel("系统日志查询参数VO")
public class SysLogVo {

    private Integer pageNum;
    private Integer pageSize;
    private String username;
    private List<String> dateList;

}
