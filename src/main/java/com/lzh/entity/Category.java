package com.lzh.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-03
 */
@Data
  @TableName("t_category")
@ApiModel(value = "Category对象", description = "")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("分类名称")
      private String name;

      @ApiModelProperty("父级分类id")
      private Integer pid;

      @ApiModelProperty("备注")
      private String remark;

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

      @ApiModelProperty("下级分类")
      @TableField(exist = false)// 表示这个字段在实体类中，不在数据库中
      private List<Category> children;

      @ApiModelProperty("上级分类名称")
      @TableField(exist = false)// 表示这个字段在实体类中，不在数据库中
      private String parentName;


}
