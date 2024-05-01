package com.lzh.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lzh.annotation.AuthAccess;
import com.lzh.common.Result;
import com.lzh.entity.Book;
import com.lzh.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.lzh.constant.DataConstant.BOOK_DEFAULT_COVER;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-12
 */
@Api(tags = "图书管理接口")
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @ApiOperation("查询图书列表")
    @GetMapping("/list/API_001")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String isbn,
                           @RequestParam(defaultValue = "") String category) {
        return Result.success(bookService.listAPI_001(pageNum, pageSize, name, isbn, category));
    }

    @ApiOperation("新增或编辑图书")
    @PostMapping("/addOrUpdate/API_002")
    public Result addOrUpdateAPI_002(@RequestBody Book book) {
        // 设置图书的分类，以字符串形式保存，如：/计算机/数据结构
        List<String> categories = book.getCategories();
        StringBuffer sb = new StringBuffer();
        if (CollUtil.isNotEmpty(categories)){
            categories.forEach(category -> sb.append(category).append("/"));
            book.setCategory(sb.substring(0,sb.lastIndexOf("/")));
        }
        // 编辑图书
        if (ObjectUtil.isNotEmpty(book.getId())){
            book.setLeaveCount(book.getTotalCount() - book.getLendCount());
        }else {
            book.setLendCount(0);
            book.setLeaveCount(book.getTotalCount());
        }
        // 设置图书默认封面
        if (StrUtil.isBlank(book.getCover())){
            book.setCover(BOOK_DEFAULT_COVER);
        }
        return Result.success(bookService.saveOrUpdate(book));
    }

    @ApiOperation("删除图书")
    @DeleteMapping("/delete/API_003/{id}")
    public Result deleteAPI_003(@PathVariable Integer id) {
        return Result.success(bookService.removeById(id));
    }

    @ApiOperation("批量删除图书")
    @PostMapping("/delete/batch/API_004")
    public Result deleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(bookService.removeByIds(ids));
    }

    @ApiOperation("导出图书")
    @GetMapping("/export/API_005")
    public void ExportAPI_005(HttpServletResponse response, @RequestParam String ids) {
        bookService.exportAPI_005(response,ids);
    }

    @ApiOperation("导入图书")
    @PostMapping("/import/API_006")
    public Result ImportAPI_006(MultipartFile file) {
        return Result.success(bookService.importAPI_006(file));
    }

    @ApiOperation("图书信息导入模板")
    @GetMapping("/export/template/API_007")
    public void ExportTemplateAPI_007(HttpServletResponse response) {
        bookService.exportTemplateAPI_007(response);
    }

    @ApiOperation("根据图书id查询图书信息")
    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable String id) {
        return Result.success(bookService.getById(id));
    }

    /**
     * 图书推荐，默认取评分前五的书籍
     * @return
     */
    @Cacheable("bookrecommend")
    @ApiOperation("图书推荐")
    @GetMapping("/recommend")
    public Result recommendBook() {
        List<Book> topFiveBooks = bookService.list().stream()
                .filter(book -> book.getRecommendScore() > 3.5)
                .sorted(Comparator.comparing(Book::getRecommendScore).reversed()) // 分数越高，推荐度越好
                .limit(5)
                .collect(Collectors.toList());
        return Result.success(topFiveBooks);
    }

    /**
     * 换一换图书推荐。随机取评分大于3.5中的5本
     * @return
     */
    @ApiOperation("图书推荐（随机）")
    @GetMapping("/change/recommend")
    public Result changeRecommendBook() {
//        Set<Integer> randomFiveBookIds = bookService.list().stream()
//                .filter(book -> book.getRecommendScore() > 3.5)
//                .map(Book::getId)
//                .collect(Collectors.toSet()) // 先转为Set去除重复元素
//                .stream() // 再次转为流以进行下一步操作
//                .sample(random) // 打乱流中的元素顺序
//                .limit(5) // 限制为五个元素
//                .collect(Collectors.toSet()); // 再次收集为Set保持不重复性
        List<Integer> randomBook = bookService.list().stream()
                .filter(book -> book.getRecommendScore() > 3.5)
                .map(Book::getId)
                .collect(Collectors.toList());
        // 随机打乱集合
        Collections.shuffle(randomBook, new Random());
        // 获取前五个元素ids
        List<Integer> randomFiveBookIds = new ArrayList<>(randomBook.subList(0, Math.min(5, randomBook.size())));
        return Result.success(bookService.listByIds(randomFiveBookIds));
    }

}

