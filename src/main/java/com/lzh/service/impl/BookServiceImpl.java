package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.Book;
import com.lzh.mapper.BookMapper;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.lzh.constant.DataConstant.BOOK_DEFAULT_COVER;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-12
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public PageData<Book> listAPI_001(Integer pageNum, Integer pageSize,
                                      String name, String isbn, String category) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StrUtil.isNotBlank(name),Book::getName,name)
                .like(StrUtil.isNotBlank(isbn),Book::getIsbn,isbn)
                .like(StrUtil.isNotBlank(category),Book::getCategory,category)
                .orderByDesc(Book::getCreateTime);

        Page<Book> page = new Page<>(pageNum,pageSize);
        bookMapper.selectPage(page,queryWrapper);
        return new PageData<>(page.getRecords(),page.getTotal());
    }

    @Override
    public void exportAPI_005(HttpServletResponse response, String ids) {
        // 部分导出
        LambdaQueryWrapper<Book> queryWrapper = null;
        if (ids.length() >0){
            String[] split = ids.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.valueOf(s));
            }
            queryWrapper = new LambdaQueryWrapper<Book>().in(idList.size() > 0,Book::getId,idList);
        }

        ServletOutputStream out = null;
        // 在内存操作，写出到浏览器
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            List<Book> bookList = bookMapper.selectList(queryWrapper);
            writer.setOnlyAlias(true);

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(bookList, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = null;
            fileName = URLEncoder.encode("图书信息", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportTemplateAPI_007(HttpServletResponse response) {
        ServletOutputStream out = null;
        // 在内存操作，写出到浏览器
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            List<Book> template = bookMapper.selectList(new QueryWrapper<Book>().lambda().last("limit 2"));

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(template, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = null;
            fileName = URLEncoder.encode("图书信息导入模板", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> importAPI_006(MultipartFile file) {
        // 图书的ISBN不能相同
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
            List<Book> books = reader.readAll(Book.class);
            Set<String> isbnSet1 = books.stream().map(Book::getIsbn).collect(Collectors.toSet());
            Set<String> isbnSet2 = list().stream().map(Book::getIsbn).collect(Collectors.toSet());

            // 对导入的数据做个过滤，名称不相同才能导入成功
            Set<String> bookSet = isbnSet1.stream()
                    .filter(isbn -> !isbnSet2.contains(isbn))
                    .collect(Collectors.toSet());

            int successCount = bookSet.size();// 导入成功的数量
            int failedCount = isbnSet1.size() - bookSet.size();// 导入失败的数量

            List<Book> bookList = books.stream()
                    .filter(book -> bookSet.contains(book.getIsbn()))
                    .peek(book -> {
                        if (StrUtil.isBlank(book.getCover())) {
                            book.setCover(BOOK_DEFAULT_COVER);
                        }
                    }).collect(Collectors.toList());
            saveBatch(bookList);
            String msgSuccess = StrUtil.format("{}条导入成功！",successCount);
            String msgError = StrUtil.format("{}条导入失败！",failedCount);

            Map<String, Object> map = new HashMap<>();
            map.put("msgSuccess",msgSuccess);
            map.put("successCount",successCount);
            map.put("msgError",msgError);
            map.put("failedCount",failedCount);
            return map;
        } catch (IOException e) {
            log.error("导入失败");
            throw new RuntimeException(e);
        }
    }
}
