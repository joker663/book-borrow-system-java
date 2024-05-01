package com.lzh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
 * @since 2024-03-03
 */
@Getter
@Setter
  @TableName("t_comment")
@ApiModel(value = "Comment对象", description = "")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("内容")
      private String content;

      @ApiModelProperty("评论人id")
      private Integer readerId;

      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      @ApiModelProperty("评论时间")
      private LocalDateTime time;

      @ApiModelProperty("父id")
      private Integer pid;

      @ApiModelProperty("最上级评论id")
      private Integer originId;

      @ApiModelProperty("关联图书的id")
      private Integer bookId;

      @TableField(exist = false)
      private String parentNickname;  // 父节点的读者昵称

      @TableField(exist = false)
      private Integer parentReaderId;  // 父节点的读者id

      @TableField(exist = false)
      private String nickname;

      @TableField(exist = false)
      private String avatarUrl;

      @TableField(exist = false)
      private List<Comment> children;

}
