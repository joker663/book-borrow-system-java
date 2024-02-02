package com.lzh.service;

import com.lzh.common.Result;
import com.lzh.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.vo.PasswordVo;
import com.lzh.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-21
 */
public interface UserService extends IService<User> {

    Result sysUserListAPI_001(Integer pageNum, Integer pageSize, String username, String phone, String nickname);

    void sysUserExportAPI_005(HttpServletResponse response,String ids);

    void sysUserExportTemplateAPI_007(HttpServletResponse response);

    Map<String, Object> sysUserImportAPI_006(MultipartFile file);

    UserVo login(UserVo userVo);

    User register(UserVo userVo);

    void sysUserUpdatePassword(PasswordVo passwordVo);
}
