package com.lzh.front.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-02
 */
@Data
@ApiModel("用户评分Vo")
public class MarkScoreVo {

    private Integer id;

    private Double score;
}
