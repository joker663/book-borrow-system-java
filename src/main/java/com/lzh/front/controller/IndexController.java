package com.lzh.front.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.common.Result;
import com.lzh.entity.Book;
import com.lzh.entity.Carousel;
import com.lzh.entity.Category;
import com.lzh.front.vo.BorrowRankingVo;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import com.lzh.service.CarouselService;
import com.lzh.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 系统前台首页数据
 * @Author: lzh
 * @Date: 2024-02-23
 */
@Api(tags = "系统前台首页数据接口")
@RestController
@RequestMapping("/front/index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService bookService;

    @Cacheable("carousel")
    @ApiOperation("首页轮播图接口")
    @GetMapping("/carousel/list")
    public Result getCarousel() {
        return Result.success(carouselService.list().stream().filter(carousel -> carousel.getState() == 1)
                .map(Carousel::getImageUrl).collect(Collectors.toList()));
    }

    @Cacheable("borrowranking")
    @ApiOperation("借阅排行接口")
    @GetMapping("/borrowranking")
    public Result borrowRanking() {
        // 根据图书id进行分组查询，并且求count 然后选前10名
        List<BorrowRankingVo> list = borrowService.borrowRanking();
        return Result.success(list);
    }

    @ApiOperation("图书检索查询分类接口，查询所有的一级分类")
    @GetMapping("/bookcategory")
    public Result bookCategory() {
        return Result.success(categoryService.list().stream().filter(category -> category.getPid()==null));
    }

    @Cacheable("bookdata")
    @ApiOperation("图书检索查询分类接口，查询所有的一级分类")
    @GetMapping("/bookdata")
    public Result bookData(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String keywords,
                           @RequestParam Integer type) {
        // type是一级分类的id
        Category category = categoryService.getById(type);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(category)){
            queryWrapper.like("category",category.getName());
        }
        if (StrUtil.isNotBlank(keywords)){
            queryWrapper.like("name",keywords)
                    .or()
                    .like("author",keywords)
                    .or()
                    .like("description",keywords)
                    .or()
                    .like("isbn",keywords);
        }
        queryWrapper.orderByDesc("recommend_score");

        Page<Book> page = new Page<>(pageNum, pageSize);
        bookService.page(page,queryWrapper);
        return Result.success(new PageData<>(page.getRecords(),page.getTotal()));
    }

}
