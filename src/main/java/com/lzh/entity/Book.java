package com.lzh.entity;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.annotation.PropIgnore;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2024-02-12
 */
@Getter
@Setter
  @TableName("t_book")
@ApiModel(value = "Book对象", description = "")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @PropIgnore
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @Alias("图书名称")
      @ApiModelProperty("图书名称")
      private String name;

      @Alias("作者")
      @ApiModelProperty("作者")
      private String author;

      @Alias("国际标准书号")
      @ApiModelProperty("国际标准书号")
      private String isbn;

      @ApiModelProperty("出版日期")
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
      private Date publishDate;

      @ApiModelProperty("描述")
      @Alias("描述")
      private String description;

      @ApiModelProperty("出版社")
      @Alias("出版社")
      private String publisher;

      @ApiModelProperty("分类")
      @Alias("分类")
      private String category;

      @ApiModelProperty("封面")
      @Alias("封面")
      private String cover;

      @ApiModelProperty("图书总量")
      @Alias("图书总量")
      private Integer totalCount;

      @ApiModelProperty("借出数量")
      @Alias("借出数量")
      private Integer lendCount;

      @ApiModelProperty("剩余数量")
      @Alias("剩余数量")
      private Integer leaveCount;

      @ApiModelProperty("创建时间")
      @Alias("创建时间")
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      @ApiModelProperty("更新时间")
      @Alias("更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;

      @ApiModelProperty("创建人")
      @Alias("创建人")
      @TableField(fill = FieldFill.INSERT)
      private Integer createBy;

      @ApiModelProperty("更新人")
      @Alias("更新人")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private Integer updateBy;

      @TableField(exist = false)
      private List<String> categories;

      @ApiModelProperty("推荐指数")
      @Alias("推荐指数（5分制）")
      private Double recommendScore;

}
