package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.Carousel;
import com.lzh.mapper.CarouselMapper;
import com.lzh.page.PageData;
import com.lzh.service.CarouselService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 轮播图表 服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public PageData<Carousel> carouseListAPI_001(Integer pageNum, Integer pageSize, String imageName) {
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StrUtil.isNotBlank(imageName),Carousel::getImageName,imageName)
                .orderByDesc(Carousel::getState);

        Page<Carousel> page = new Page<>(pageNum,pageSize);
        carouselMapper.selectPage(page,queryWrapper);
        return new PageData<>(page.getRecords(),page.getTotal());
    }
}
