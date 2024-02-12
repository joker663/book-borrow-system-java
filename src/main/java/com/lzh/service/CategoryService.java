package com.lzh.service;

import com.lzh.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-03
 */
public interface CategoryService extends IService<Category> {

    List<Category> listAPI_001(String name);
}
