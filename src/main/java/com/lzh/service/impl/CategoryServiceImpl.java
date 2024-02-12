package com.lzh.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.entity.Category;
import com.lzh.mapper.CategoryMapper;
import com.lzh.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-03
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listAPI_001(String name) {
        // 根据条件查询所有的菜单数据
        List<Category> list = list(new QueryWrapper<Category>()
                .lambda()
                .like(StrUtil.isNotBlank(name), Category::getName, name));// 只能按一级分类查询

        return createTree(null, list);// null 表示从第一级开始递归
    }

    /**
     * 递归查询一级分类下面的所有子分类
     * @param pid
     * @param categories
     * @return
     */
    private List<Category> createTree(Integer pid,List<Category> categories){
        //把所有分类转Map方便后续后续父级分类
        Map<Integer, Category> categoryListMap = list().stream()
                .collect(Collectors.toMap(Category::getId
                        , category -> category
                        , (pre, next) -> next));

        //把categories转Map方便后续后续父级名称
        Map<Integer, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId
                        , category -> category
                        , (pre, next) -> next));

        // treeList：最终返回的树级List结构
        List<Category> treeList = new ArrayList<>();
        for (Category category : categories) {// 遍历满足条件的
            if (pid == null){// pid == null，表示从第一级（分类）开始递归。（相当于拿到一个一级分类）
                if (null == category.getPid()){// 如果拿到的这个分类他的pid==null，说明这个分类就是一级分类
                    treeList.add(category);// 向集合中加入这个一级分类
                    // 递归的方式设置这个一级分类的下级分类。此时第一个参数，也就是pid就变成当前这个一级分类的id了
                    category.setChildren(createTree(category.getId(),categories));
                }else {// 加个else 如果null != category.getPid() 就根据category查出他的父级，然后再加入treeList中（解决只能查出一级分类的问题）
                    // 再写一个递归
                    if (categoryListMap.size() > categories.size()){// 只有按条件查询的时候，才显示按条件查询的数据
                        List<Category> categoryList = new ArrayList<>();// 这个集合存放的是当前分类，以及他的所有父级分类，但是不是树形结构
                        categoryList.add(category);
                        categoryList.addAll(getParentCategories(category, categoryListMap));
                        treeList.addAll(getCategoryTree(null,categoryList));
                        break;
                    }
                }
            }else {// else 中的操作是对if中拿到的一级分类 然后设置他的下级分类（给一级分类设置下级分类）
                if (pid.equals(category.getPid())){
                    treeList.add(category);
                    category.setChildren(createTree(category.getId(),categories));

                    // pid不为空的话，设置上级分类名称
                    // 根据pid查出父级分类
                    Category category1 = categoryMap.get(pid);
                    if (ObjectUtil.isNotNull(category1)){
                        category.setParentName(category1.getName());
                    }
                }
            }
            // 如果没有下级分类的话，就让children为null，而不是[] 是为了让前端更好的展示
            if (CollUtil.isEmpty(category.getChildren())){
                category.setChildren(null);
            }
        }
        return treeList;
    }

    /**
     * 根据下级分类获取所有的父级分类
     * @param category
     * @param categoryListMap
     * @return
     */
    public List<Category> getParentCategories(Category category, Map<Integer, Category> categoryListMap) {
        List<Category> parentCategories = new ArrayList<>();
        if (category.getPid() != null) {
            Category parentCategory = categoryListMap.get(category.getPid());
            parentCategories.add(parentCategory);
            parentCategories.addAll(getParentCategories(parentCategory, categoryListMap));
        }
        return parentCategories;
    }

    /**
     * 根据分类集合获取分类树
     * @param categoryList
     * @return
     */
    public List<Category> getCategoryTree(Integer pid, List<Category> categoryList) {
        List<Category> treeList = new ArrayList<>();
        for (Category category : categoryList) {
            if (pid == null){
                if (category.getPid() == null) {
                    treeList.add(category);
                    category.setChildren(getCategoryTree(category.getId(), categoryList));
                }
            } else {
                if (pid.equals(category.getPid())) {
                    treeList.add(category);
                    category.setChildren(getCategoryTree(category.getId(), categoryList));
                }
            }
        }
        return treeList;
    }

}
