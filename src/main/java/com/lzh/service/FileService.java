package com.lzh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.page.PageData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-28
 */
public interface FileService extends IService<Files> {

    String upload(MultipartFile file) throws IOException;

    void download(String fileUUID, HttpServletResponse response) throws IOException;

    PageData<Files> findPage(Integer pageNum, Integer pageSize, String name);
}
