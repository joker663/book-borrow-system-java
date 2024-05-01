package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.Book;
import com.lzh.entity.Borrow;
import com.lzh.entity.Reader;
import com.lzh.exception.MyException;
import com.lzh.front.vo.ReaderVo;
import com.lzh.mapper.ReaderMapper;
import com.lzh.page.PageData;
import com.lzh.service.BookService;
import com.lzh.service.BorrowService;
import com.lzh.service.ReaderService;
import com.lzh.utils.TokenUtil;
import com.lzh.vo.PasswordVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.lzh.constant.DataConstant.READER_DEFAULT_AVATAR;

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

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookService bookService;

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

    @Override
    public ReaderVo login(ReaderVo readerVo) {
        // 解码 Base64 编码的密码
        String reversedPassword = cn.hutool.core.codec.Base64.decodeStr(StrUtil.reverse(readerVo.getPassword()));

        List<Reader> readerList = readerMapper.selectList(new QueryWrapper<Reader>()
                .lambda()
                .eq(Reader::getUsername, readerVo.getUsername()));
        if (readerList.size() > 0){
            Reader reader = readerList.get(0);
            if (!SecureUtil.md5(reversedPassword).equals(reader.getPassword())){
                throw new MyException(CodeConstant.CODE_401,"用户名或密码不正确");
            }
            // 如果读者同时有三本图书未归还，将封禁读者账号，并强制归还所有已逾期图书。
            List<Borrow> borrowList = borrowService.list(new QueryWrapper<Borrow>()
                    .lambda()
                    .eq(Borrow::getReaderId, reader.getId())
                    .eq(Borrow::getState,2));
            if (borrowList.size() >= 3) {
                // 封禁读者账号
                reader.setState(0);
                updateById(reader);
                //强制归还所有已逾期图书
                for (Borrow borrow : borrowList) {
                    forceBackBook(borrow.getId());
                }
            }

            if (reader.getState() == 0){
                throw new MyException(CodeConstant.CODE_210,"当前账号处于封禁状态，请联系管理员!");
            }

            BeanUtils.copyProperties(reader, readerVo);
            // 用户登录之后，把token也返回
            String token = TokenUtil.getToken(reader.getId().toString(), reader.getPassword());
            readerVo.setToken(token);
            return readerVo;
        }else {
            throw new MyException("210","用户名或密码错误");
        }
    }

    @Override
    public Reader register(ReaderVo readerVo) {
        // 用户密码 md5加密
        String reversedPassword = cn.hutool.core.codec.Base64.decodeStr(StrUtil.reverse(readerVo.getPassword()));
        readerVo.setPassword(SecureUtil.md5(reversedPassword));
        Reader reader = getReaderInfo(readerVo);
        if (reader == null) {
            reader = new Reader();
            BeanUtils.copyProperties(readerVo, reader);
            if (reader.getNickname() == null) {
                reader.setNickname(reader.getUsername());
            }
            if (StrUtil.isBlank(reader.getAvatarUrl())){
                reader.setAvatarUrl(READER_DEFAULT_AVATAR);
            }
            reader.setReaderNumber(reader.getUsername() + LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            save(reader);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new MyException(CodeConstant.CODE_210, "用户名已存在");
        }
        return reader;
    }

    @Override
    public void updatePassword(PasswordVo passwordVo) {
        // 解码 Base64 编码的密码
        String reversedPassword = cn.hutool.core.codec.Base64.decodeStr(StrUtil.reverse(passwordVo.getPassword()));
        String reversedNewPassword = cn.hutool.core.codec.Base64.decodeStr(StrUtil.reverse(passwordVo.getNewPassword()));

        passwordVo.setPassword(SecureUtil.md5(reversedPassword));// 新增用户默认密码（新增时，未填写密码默认密码为123）
        passwordVo.setNewPassword(SecureUtil.md5(reversedNewPassword));// 新增用户默认密码（新增时，未填写密码默认密码为123）

        int update = readerMapper.updatePassword(passwordVo);
        if (update < 1){
            throw new MyException(CodeConstant.CODE_210,"密码错误");
        }
    }

    /**
     * 根据username和password查询读者
     * @param readerVo
     * @return
     */
    private Reader getReaderInfo(ReaderVo readerVo) {
        List<Reader> readerList = readerMapper.selectList(new QueryWrapper<Reader>()
                .lambda()
                .eq(Reader::getUsername, readerVo.getUsername()));
        Reader reader = null;
        if (readerList.size() > 0){
            reader = readerList.get(0);
        }
        return reader;
    }

    public void forceBackBook(Integer id){
        Borrow borrow = borrowService.getById(id);
        // 将borrow表中的借阅记录状态改为1(已归还)
        borrow.setState(1);
        borrow.setBackTime(LocalDateTime.now());
        borrowService.updateById(borrow);
        // 将图书的借阅数量减一，可借阅数量+1
        Book book = bookService.getById(borrow.getBookId());
        book.setLendCount(book.getLendCount() - 1);
        book.setLeaveCount(book.getLeaveCount() + 1);
        bookService.updateById(book);
        // 将读者的可借阅次数减一
        Reader reader = getById(borrow.getReaderId());
        reader.setHaveTimes(reader.getHaveTimes() - 1);
        updateById(reader);
    }
}


