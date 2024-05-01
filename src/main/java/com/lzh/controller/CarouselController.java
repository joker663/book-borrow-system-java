package com.lzh.controller;

import com.lzh.common.Result;
import com.lzh.entity.Carousel;
import com.lzh.oss.AliOSSUtils;
import com.lzh.service.CarouselService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 轮播图表 前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-25
 */
@Api("轮播图管理接口")
@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @ApiOperation("查询轮播图列表")
    @GetMapping("/list/API_001")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String imageName) {
        return Result.success(carouselService.carouseListAPI_001(pageNum, pageSize, imageName));
    }

    @ApiOperation("上传轮播图")
    @PostMapping("/add/API_002")
    public String addAPI_002(@RequestParam MultipartFile file) throws IOException {
        String imageUrl = aliOSSUtils.upload(file);
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        String imageType = imageName.substring(imageName.lastIndexOf("."));

        Carousel carousel = new Carousel();
        carousel.setImageUrl(imageUrl);
        carousel.setImageName(imageName);
        carousel.setImageType(imageType);
        carouselService.save(carousel);
        return imageUrl;
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/delete/API_003/{id}")
    public Result deleteAPI_003(@PathVariable Integer id) {
        return Result.success(carouselService.removeById(id));
    }

    @ApiOperation("批量删除轮播图")
    @PostMapping("/delete/batch/API_004")
    public Result deleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(carouselService.removeByIds(ids));
    }

    @ApiOperation("根据轮播图id查询轮播图信息")
    @GetMapping("/getById/{id}")
    public Result getReaderById(@PathVariable String id) {
        return Result.success(carouselService.getById(id));
    }

    @ApiOperation("启用轮播图开关")
    @PostMapping("/state/update")
    public Result update(@RequestBody Carousel carousel){
        return Result.success(carouselService.updateById(carousel));
    }

}

