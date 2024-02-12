package com.lzh.entity;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.annotation.PropIgnore;
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
 * @since 2024-02-02
 */
@Getter
@Setter
  @TableName("t_reader")
@ApiModel(value = "Reader对象", description = "")
public class Reader implements Serializable {

    private static final long serialVersionUID = 1L;

      @PropIgnore
      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @Alias("读者编号")
      @ApiModelProperty("读者编号")
      private String readerNumber;

      @Alias("用户名")
      @ApiModelProperty("用户名")
      private String username;

      @Alias("密码")
      @ApiModelProperty("密码")
      private String password;

      @Alias("昵称")
      @ApiModelProperty("昵称")
      private String nickname;

      @Alias("邮箱")
      @ApiModelProperty("邮箱")
      private String email;

      @Alias("电话")
      @ApiModelProperty("电话")
      private String phone;

      @Alias("地址")
      @ApiModelProperty("地址")
      private String address;

      @Alias("头像")
      @ApiModelProperty("头像")
      private String avatarUrl;

      @Alias("性别：0-女,1-男")
      @ApiModelProperty("性别 0 女，1 男")
      private Integer gender;

      @Alias("账号状态")
      @ApiModelProperty("账号状态")
      private Integer state;

      @Alias("创建时间")
      @ApiModelProperty("创建时间")
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      @Alias("更新时间")
      @ApiModelProperty("更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;

      @Alias("创建人")
      @ApiModelProperty("创建人")
      @TableField(fill = FieldFill.INSERT)
      private Integer createBy;

      @Alias("更新人")
      @ApiModelProperty("更新人")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private Integer updateBy;

}
