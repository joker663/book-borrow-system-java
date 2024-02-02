package com.lzh.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.common.Result;
import com.lzh.constant.DictConstant;
import com.lzh.entity.Dict;
import com.lzh.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-29
 */
@Api(tags = "字典接口")
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("图标字典接口")
    @GetMapping("/icons")
    public Result getIcons(){
        return Result.success(dictService.list(new QueryWrapper<Dict>()
                .lambda()
                .eq(Dict::getType, DictConstant.DICT_TYPE_ICON)));
    }

}

