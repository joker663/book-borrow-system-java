package com.lzh.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.common.Result;
import com.lzh.entity.Book;
import com.lzh.entity.Comment;
import com.lzh.entity.Reader;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.lzh.service.CommentService;
import com.lzh.service.ReaderService;
import com.lzh.utils.TokenUtil;
import com.lzh.vo.CommentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-03
 */
@Api("读者评论接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @ApiOperation("评论列表")
    @GetMapping("/treeList/{bookId}")
    public Result findCommentTree(@PathVariable Integer bookId) {
        List<Comment> bookComments = commentService.findCommentDetail(bookId);  // 查询所有的评论和回复数据
        // 查询评论数据（不包括回复）
        List<Comment> originList = bookComments.stream()
                .filter(comment -> comment.getOriginId() == null)
                .collect(Collectors.toList());

        // 设置评论数据的子节点，也就是回复内容
        for (Comment origin : originList) {// 遍历一级评论
            List<Comment> comments = bookComments.stream()
                    .filter(comment -> origin.getId().equals(comment.getOriginId()))
                    .collect(Collectors.toList());  // 拿到这个一级评论的所有回复（表示回复对象集合）

            // 一级评论下可能有很多回复，每个回复可能又有其他回复。这种情况下，这些回复的直接父级就是也属于回复，而不是最上层的评论。
            // 针对这种情况，设置他的父级回复
            comments.forEach(comment -> {
                Optional<Comment> pComment = bookComments.stream()
                        .filter(c1 -> c1.getId().equals(comment.getPid()))
                        .findFirst();  // 拿到一级评论下某个回复的父级评论（找到当前评论的父级）

                pComment.ifPresent((v -> {  // 拿到一级评论下某个回复的父级评论，并设置父级的id和name，用于@展示（找到父级评论的读者id和读者昵称，并设置给当前的回复对象）
                    comment.setParentReaderId(v.getReaderId());
                    comment.setParentNickname(v.getNickname());
                }));
            });
            origin.setChildren(comments);
        }
        return Result.success(originList);
    }

    @ApiOperation("新增评论")
    @PostMapping("/add")
    public Result save(@RequestBody Comment comment) {
        // 新增评论分两种情况：1、是作为一级评论 2、是作为回复
        if (comment.getId() == null) { // 新增评论
            comment.setReaderId(Objects.requireNonNull(TokenUtil.getCurrentReader()).getId());
            comment.setTime(LocalDateTime.now());

            if (comment.getPid() != null) {  // 判断如果是回复，进行处理
                Integer pid = comment.getPid();
                Comment pComment = commentService.getById(pid);
                if (pComment.getOriginId() != null) {  // 如果当前回复的父级有祖宗，那么就设置相同的祖宗
                    comment.setOriginId(pComment.getOriginId());
                } else {  // 否则就设置父级为当前回复的祖宗
                    comment.setOriginId(comment.getPid());
                }
            }
        }
        commentService.save(comment);
        return Result.success();
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        List<Comment> subcommentList = commentService.list(new QueryWrapper<Comment>().lambda().eq(Comment::getOriginId, id));
        commentService.removeById(id);
        commentService.removeByIds(subcommentList);
        return Result.success();
    }

    @ApiOperation("评论列表")
    @GetMapping("/admin/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam(defaultValue = "") String keywords) {
        // 获取读者Map集合（为了返回值时提高效率）
        Map<Integer, Reader> readerMap = readerService.list().stream()
                .collect(Collectors.toMap(Reader::getId, reader -> reader, (pre, next) -> next));
        // 获取图书Map集合（为了返回值时提高效率）
        Map<Integer, Book> bookMap = bookService.list().stream()
                .collect(Collectors.toMap(Book::getId, book -> book, (pre, next) -> next));

        LambdaQueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>()
                .lambda()
                .like(StrUtil.isNotBlank(keywords), Comment::getContent, keywords)
                .orderByDesc(Comment::getTime);

        Page<Comment> page = new Page<>(pageNum, pageSize);
        commentService.page(page,queryWrapper);

        List<CommentVo> commentVoList = new ArrayList<>();
        if (page.getRecords().size() > 0){
            page.getRecords().forEach(comment -> {
                CommentVo commentVo = new CommentVo();
                commentVo.setId(comment.getId());
                commentVo.setTime(comment.getTime());
                commentVo.setContent(comment.getContent());
                Optional.ofNullable(readerMap.get(comment.getReaderId()))
                        .ifPresent(reader -> commentVo.setUsername(reader.getUsername()));
                Optional.ofNullable(readerMap.get(comment.getReaderId()))
                        .ifPresent(reader -> commentVo.setNickname(reader.getNickname()));
                Optional.ofNullable(bookMap.get(comment.getBookId()))
                        .ifPresent(book -> commentVo.setBookName(book.getName()));
                Optional.ofNullable(bookMap.get(comment.getBookId()))
                        .ifPresent(book -> commentVo.setIsbn(book.getIsbn()));
                commentVoList.add(commentVo);
            });
        }

        return Result.success(new PageData<CommentVo>(commentVoList, page.getTotal()));
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/admin/delete/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        return Result.success(commentService.removeById(id));
    }

    @ApiOperation("批量删除评论")
    @PostMapping("/admin/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(commentService.removeByIds(ids));
    }


}

