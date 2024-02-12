package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.entity.Reader;
import com.lzh.mapper.ReaderMapper;
import com.lzh.page.PageData;
import com.lzh.service.ReaderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-02
 */
@Service
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements ReaderService {

    @Autowired
    private ReaderMapper readerMapper;

    @Override
    public PageData<Reader> readerListAPI_001(Integer pageNum, Integer pageSize, String username,
                                              String phone, String nickname, String readerNumber) {
        QueryWrapper<Reader> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StrUtil.isNotBlank(username),Reader::getUsername,username)
                .like(StrUtil.isNotBlank(phone),Reader::getPhone,phone)
                .like(StrUtil.isNotBlank(nickname),Reader::getNickname,nickname)
                .like(StrUtil.isNotBlank(readerNumber),Reader::getReaderNumber,readerNumber)
                .orderByDesc(Reader::getCreateTime);

        Page<Reader> page = new Page<>(pageNum,pageSize);
        readerMapper.selectPage(page,queryWrapper);
        return new PageData<>(page.getRecords(),page.getTotal());
    }

    @Override
    public void exportReaderAPI_005(HttpServletResponse response, String ids) {
        // 部分导出
        LambdaQueryWrapper<Reader> queryWrapper = null;
        if (ids.length() >0){
            String[] split = ids.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.valueOf(s));
            }
            queryWrapper = new LambdaQueryWrapper<Reader>().in(idList.size() > 0,Reader::getId,idList);
        }

        ServletOutputStream out = null;
        // 在内存操作，写出到浏览器
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            List<Reader> readerList = readerMapper.selectList(queryWrapper);
            writer.setOnlyAlias(true);

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(readerList, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = null;
            fileName = URLEncoder.encode("读者信息", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


