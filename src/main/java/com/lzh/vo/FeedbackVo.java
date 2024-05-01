package com.lzh.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-05
 */
@Data
@ApiModel("留言反馈VO")
public class FeedbackVo {

    private Integer readerId;

    private String text;

    private String color;

    private String position;

}
