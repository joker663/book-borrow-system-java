package com.lzh.front.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-02
 */
@Data
@ApiModel("续借图书VO")
public class RenewBookVo {

    private Integer id;
    private Integer renewDays;

}
