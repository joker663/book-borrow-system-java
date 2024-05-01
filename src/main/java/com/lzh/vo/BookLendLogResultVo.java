package com.lzh.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-02-25
 */
@Data
@ApiModel("图书借阅日志返回值VO")
public class BookLendLogResultVo {

    private Integer id;
    private String username;
    private String readerNumber;
    private String bookName;
    private String isbn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endTime;
    private Integer leaveDays;// 剩余天数
    private Integer state;// 剩余天数
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime backTime;
    private Double score;

}
