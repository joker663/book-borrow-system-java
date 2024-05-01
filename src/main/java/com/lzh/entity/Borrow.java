package com.lzh.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Getter
@Setter
  @TableName("t_borrow")
@ApiModel(value = "Borrow对象", description = "")
public class Borrow implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("读者id")
      private Integer readerId;

      @ApiModelProperty("图书id")
      private Integer bookId;

      @ApiModelProperty("借阅持续时间")
      private Integer duringTime;

      @JsonFormat(pattern = "yyyy-MM-dd")
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      @ApiModelProperty("借阅开始日期")
      private LocalDateTime beginTime;

      @JsonFormat(pattern = "yyyy-MM-dd")
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      @ApiModelProperty("借阅结束日期")
      private LocalDateTime endTime;

      @ApiModelProperty("创建时间")
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      @ApiModelProperty("更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;

      @ApiModelProperty("借阅状态：0借阅中 1已归还 2已逾期")
      private Integer state;

      @JsonFormat(pattern = "yyyy-MM-dd")
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      @ApiModelProperty("借阅结束日期")
      private LocalDateTime backTime;

      @ApiModelProperty("是否删除，0未删除 1删除")
      private Integer isDelete;

      @ApiModelProperty("读者评分")
      private Double score;

}
