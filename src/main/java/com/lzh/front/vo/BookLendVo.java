package com.lzh.front.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-25
 */
@Data
@ApiModel("图书借阅VO")
public class BookLendVo {

    private Integer readerId;

    private String bookId;
}
