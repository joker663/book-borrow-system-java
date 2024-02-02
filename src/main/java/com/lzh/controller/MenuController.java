package com.lzh.controller;


import com.lzh.common.Result;
import com.lzh.entity.Menu;
import com.lzh.service.MenuService;
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
 * @since 2024-01-28
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation("查询菜单列表")
    @GetMapping("/sysMenu/list/API_001")
    public Result findPage(@RequestParam(defaultValue = "") String name) {
        return Result.success(menuService.sysMenuListAPI_001(name));
    }

    @ApiOperation("新增或编辑菜单")
    @PostMapping("/sysMenu/addOrUpdate/API_002")
    public Result sysMenuAddOrUpdateAPI_002(@RequestBody Menu menu) {
        return Result.success(menuService.saveOrUpdate(menu));
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("/sysMenu/delete/API_003/{id}")
    public Result sysMenuDeleteAPI_003(@PathVariable Integer id) {
        return Result.success(menuService.removeById(id));
    }

    @ApiOperation("批量删除菜单")
    @PostMapping("/sysMenu/delete/batch/API_004")
    public Result sysMenuDeleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(menuService.removeByIds(ids));
    }

    @GetMapping("/sysMenu/findAll/ids/API_005")
    public Result sysMenuFindAllIdsAPI_005(){
        return Result.success(menuService.list().stream().map(Menu::getId));
    }

}

