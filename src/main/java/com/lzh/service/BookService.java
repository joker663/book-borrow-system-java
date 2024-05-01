package com.lzh.service;

import com.lzh.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.page.PageData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-12
 */
public interface BookService extends IService<Book> {

    PageData<Book> listAPI_001(Integer pageNum, Integer pageSize, String name, String isbn, String category);

    void exportAPI_005(HttpServletResponse response, String ids);

    void exportTemplateAPI_007(HttpServletResponse response);

    Map<String, Object> importAPI_006(MultipartFile file);
}
