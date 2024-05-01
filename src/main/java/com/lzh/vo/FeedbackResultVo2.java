package com.lzh.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-06
 */
@Data
@ApiModel("留言反馈返回值VO")
public class FeedbackResultVo2 {

    private Integer id;

    private String text;

    private LocalDateTime createTime;

    private String username;

    private String nickname;

}
