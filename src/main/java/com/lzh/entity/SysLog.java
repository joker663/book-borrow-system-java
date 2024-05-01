package com.lzh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-05-01
 */
@Getter
@Setter
  @TableName("sys_log")
@ApiModel(value = "Log对象", description = "")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("主键id")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("操作内容")
      private String name;

      @ApiModelProperty("操作时间")
      private LocalDateTime time;

      @ApiModelProperty("操作人")
      private String username;

      @ApiModelProperty("操作人IP")
      private String ip;

      @ApiModelProperty("角色")
      private String role;


}
