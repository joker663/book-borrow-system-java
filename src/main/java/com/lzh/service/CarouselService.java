package com.lzh.service;

import com.lzh.entity.Carousel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.page.PageData;

/**
 * <p>
 * 轮播图表 服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
public interface CarouselService extends IService<Carousel> {

    PageData<Carousel> carouseListAPI_001(Integer pageNum, Integer pageSize, String imageName);
}
