package com.lzh.service;

import com.lzh.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.page.PageData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-03-07
 */
public interface NoticeService extends IService<Notice> {

    PageData<Notice> noticeListAPI_001(Integer pageNum, Integer pageSize, String keywords);
}
