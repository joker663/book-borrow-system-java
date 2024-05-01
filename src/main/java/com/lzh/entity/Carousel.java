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
 * 轮播图表
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Getter
@Setter
  @TableName("t_carousel")
@ApiModel(value = "Carousel对象", description = "轮播图表")
public class Carousel implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("图片url")
      private String imageUrl;

      @ApiModelProperty("图片名")
      private String imageName;

      @ApiModelProperty("图片类型")
      private String imageType;

      @ApiModelProperty("是否启用 1是 0否")
      private Integer state;

      @ApiModelProperty("是否删除，0未删除 1删除")
      private Integer isDelete;

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
