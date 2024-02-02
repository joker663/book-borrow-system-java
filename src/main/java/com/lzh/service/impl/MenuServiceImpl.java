package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.entity.Menu;
import com.lzh.mapper.MenuMapper;
import com.lzh.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-28
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> sysMenuListAPI_001(String name) {
        // 查询所有的菜单数据
        List<Menu> list = list(new QueryWrapper<Menu>()
                .lambda()
                .orderByDesc(Menu::getSortNum)
                .like(StrUtil.isNotBlank(name), Menu::getName, name));
        // 找出pid为null的菜单（即一级菜单）
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        // 找出一级菜单的子菜单
        for (Menu parentNode : parentNodes) {
            parentNode.setChildren(
                    list.stream()
                    .filter(m -> parentNode.getId().equals(m.getPid()))
                    .collect(Collectors.toList()));
            // 做菜单，可以多级循环（对象中有集合属性，这个集合的类型又是一个对象；或者递归
            //（如果不知道有几级菜单的话，可以用递归）
        }
        return parentNodes;
    }
}
