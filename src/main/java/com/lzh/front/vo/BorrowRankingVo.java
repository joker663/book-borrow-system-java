package com.lzh.front.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description:
 * @Author: lzh
 * @Date: 2024-03-09
 */
@Data
@ApiModel("图书排行VO")
public class BorrowRankingVo {

    private Integer bookId;

    private String name;

    private Integer borrowCount;

}
