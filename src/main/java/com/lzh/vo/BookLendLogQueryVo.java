package com.lzh.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-25
 */
@Data
@ApiModel("图书借阅日志查询参数VO")
public class BookLendLogQueryVo {

    private Integer pageNum;
    private Integer pageSize;
    private String username;
    private String bookName;
    private List<String> borrowDateList;
    private List<String> backDateList;

}
