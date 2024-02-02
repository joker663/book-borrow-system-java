package com.lzh.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.Role;
import com.lzh.entity.RoleMenu;
import com.lzh.service.RoleMenuService;
import com.lzh.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-28
 */
@Api(tags = "角色接口")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @ApiOperation("查询角色列表")
    @GetMapping("/sysRole/list/API_001")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        return Result.success(roleService.sysRoleListAPI_001(pageNum, pageSize, name));
    }

    @ApiOperation("新增或编辑角色")
    @PostMapping("/sysRole/addOrUpdate/API_002")
    public Result sysRoleAddOrUpdateAPI_002(@RequestBody Role role) {
        // 角色名唯一
        Set<String> collect = roleService.list()
                .stream()
                .filter(o -> !Objects.equals(o.getId(), role.getId()))
                .map(Role::getName)
                .collect(Collectors.toSet());
        if (collect.contains(role.getName())){
            return Result.error(CodeConstant.CODE_400,"角色名已存在");
        }
        return Result.success(roleService.saveOrUpdate(role));
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/sysRole/delete/API_003/{id}")
    public Result sysRoleDeleteAPI_003(@PathVariable Integer id) {
        return Result.success(roleService.removeById(id));
    }

    @ApiOperation("批量删除角色")
    @PostMapping("/sysRole/delete/batch/API_004")
    public Result sysRoleDeleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(roleService.removeByIds(ids));
    }

    /**
     * 绑定角色和菜单的关系
     * @param roleId
     * @param menuIds
     * @return
     */
    @ApiOperation("为角色分配菜单")
    @PostMapping("/roleMenu/{roleId}")
    public Result bindRoleMenu(@PathVariable Integer roleId,@RequestBody List<Integer> menuIds){
        roleService.bindRoleMenu(roleId,menuIds);
        return Result.success();
    }

    @ApiOperation("获取这个角色的当前菜单，用于数据回显")
    @GetMapping("/roleMenu/getRoleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId){
        return Result.success(roleMenuService.list(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getRoleId,roleId))
                .stream()
                .map(roleMenu -> roleMenu.getMenuId())
                .collect(Collectors.toList()));
    }

    @GetMapping("/sysRole/getAll")
    public Result findAll() {
        return Result.success(roleService.list());
    }

}

