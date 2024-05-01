package com.lzh.service;

import com.lzh.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-03
 */
public interface CommentService extends IService<Comment> {

    List<Comment> findCommentDetail(Integer bookId);
}
