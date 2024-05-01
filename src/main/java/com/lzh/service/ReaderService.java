package com.lzh.service;

import com.lzh.entity.Reader;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.front.vo.ReaderVo;
import com.lzh.page.PageData;
import com.lzh.vo.PasswordVo;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-02
 */
public interface ReaderService extends IService<Reader> {

    PageData<Reader> readerListAPI_001(Integer pageNum, Integer pageSize, String username, String phone, String nickname, String readerNumber);

    void exportReaderAPI_005(HttpServletResponse response, String ids);

    ReaderVo login(ReaderVo readerVo);

    Reader register(ReaderVo readerVo);

    void updatePassword(PasswordVo passwordVo);
}
