package com.lzh.controller;


import com.lzh.common.Result;
import com.lzh.entity.Category;
import com.lzh.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-03
 */
@Api(tags = "图书分类管理接口")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("查询图书分类列表")
    @GetMapping("/list/API_001")
    public Result findPage(@RequestParam(defaultValue = "") String name) {
        return Result.success(categoryService.listAPI_001(name));
    }

    @ApiOperation("新增或编辑图书分类")
    @PostMapping("/addOrUpdate/API_002")
    public Result addOrUpdateAPI_002(@RequestBody Category category) {
        return Result.success(categoryService.saveOrUpdate(category));
    }

    @ApiOperation("删除图书分类")
    @DeleteMapping("/delete/API_003/{id}")
    public Result deleteAPI_003(@PathVariable Integer id) {
        return Result.success(categoryService.removeById(id));
    }

    @ApiOperation("批量删除图书分类")
    @PostMapping("/delete/batch/API_004")
    public Result deleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(categoryService.removeByIds(ids));
    }

    @GetMapping("/findAll/ids/API_005")
    public Result findAllIdsAPI_005(){
        return Result.success(categoryService.list().stream().map(Category::getId));
    }


}

