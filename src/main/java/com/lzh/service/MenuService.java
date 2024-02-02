package com.lzh.service;

import com.lzh.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-28
 */
public interface MenuService extends IService<Menu> {

    List<Menu> sysMenuListAPI_001(String name);
}
