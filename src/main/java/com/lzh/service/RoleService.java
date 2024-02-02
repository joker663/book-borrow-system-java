package com.lzh.service;

import com.lzh.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.page.PageData;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-28
 */
public interface RoleService extends IService<Role> {

    PageData<Role> sysRoleListAPI_001(Integer pageNum, Integer pageSize, String name);

    void bindRoleMenu(Integer roleId, List<Integer> menuIds);
}
