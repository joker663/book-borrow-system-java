package com.lzh.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-05
 */
@Getter
@Setter
  @TableName("t_feedback")
@ApiModel(value = "Feedback对象", description = "")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("读者id")
      private Integer readerId;

      @ApiModelProperty("留言内容")
      private String text;

      @ApiModelProperty("文字颜色")
      private String color;

      @ApiModelProperty("文字位置")
      private String position;

      @ApiModelProperty("创建时间")
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      @ApiModelProperty("更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;

      @ApiModelProperty("创建人")
      @TableField(fill = FieldFill.INSERT)
      private Integer createBy;

      @ApiModelProperty("更新人")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private Integer updateBy;


}
