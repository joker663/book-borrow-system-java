package com.lzh.controller;

import com.lzh.common.Result;
import com.lzh.entity.Files;
import com.lzh.oss.AliOSSUtils;
import com.lzh.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: 文件上传相关接口
 * @Author: lzh
 * @Date: 2024-01-27
 */
@Api(tags = "文件管理接口")
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private AliOSSUtils aliOSSUtils;

    /**
     * 文件上传接口
     * @param file  前端传递过来的文件
     * @return
     * @throws IOException
     */
    @ApiOperation("上传文件（本地）")
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        return fileService.upload(file);
    }

    @ApiOperation("上传文件（阿里云OSS）")
    @PostMapping("/upload/oss")
    public String uploadOss(@RequestParam MultipartFile file) throws IOException {
        return aliOSSUtils.upload(file);
    }

    /**
     * 文件下载接口   http://localhost:8080/file/{fileUUID}
     * @param fileUUID
     * @param response
     * @throws IOException
     */
    @ApiOperation("下载文件")
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        fileService.download(fileUUID,response);
    }

    /**
     * 分页查询接口
     * @param pageNum
     * @param pageSize
     * @param name 文件名
     * @return
     */
//    @AuthAccess
    @ApiOperation("文件列表list接口")
    @GetMapping("/list")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        return Result.success(fileService.findPage(pageNum,pageSize,name));
    }

    @ApiOperation("启用开关")
    @PostMapping("/enable/update")
    public Result update(@RequestBody Files files) {
        fileService.updateById(files);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(fileService.getById(id));
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        fileService.removeById(id);
        return Result.success();
    }

    @ApiOperation("批量删除文件")
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(fileService.removeByIds(ids));
    }

}
