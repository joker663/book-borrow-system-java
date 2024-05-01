package com.lzh.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-06
 */
@Data
@ApiModel("评论管理VO")
public class CommentVo {

    private Integer id;

    private String content;

    private LocalDateTime time;

    private String username;

    private String nickname;

    private String bookName;

    private String isbn;

}
