package com.lzh.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.User;
import com.lzh.service.UserService;
import com.lzh.vo.PasswordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.lzh.constant.DataConstant.ADMIN_DEFAULT_AVATAR;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-21
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("查询用户列表")
    @GetMapping("/sysUser/list/API_001")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String phone,
                           @RequestParam(defaultValue = "") String nickname) {
        return userService.sysUserListAPI_001(pageNum, pageSize, username, phone, nickname);
    }

    @ApiOperation("新增或编辑用户")
    @PostMapping("/sysUser/addOrUpdate/API_002")
    public Result sysUserAddOrUpdateAPI_002(@RequestBody User user) {
        if (user.getId() == null && user.getPassword() == null) {
            user.setPassword(SecureUtil.md5("123"));// 新增用户默认密码（新增时，未填写密码默认密码为123）
            if (StrUtil.isBlank(user.getAvatarUrl())){// 默认头像
                user.setAvatarUrl(ADMIN_DEFAULT_AVATAR);
            }
        }
//        if (StrUtil.isBlank(user.getAvatarUrl())){
//            user.setAvatarUrl("");//给用户默认头像
//        }
        // 用户名唯一
        // filter(o -> !Objects.equals(o.getId(), user.getId())) 是为了排除当前这条记录，否则在修改的时候会提示用户已存在，修改失败
        Set<String> collect = userService.list().stream().filter(o -> !Objects.equals(o.getId(), user.getId())).map(User::getUsername).collect(Collectors.toSet());
        // 方式二：只需要判断list.size() > 0就说明用户名重复
//        List<User> list = userService.list(new QueryWrapper<User>().lambda()
//                .ne(StrUtil.isNotBlank(user.getId().toString()), User::getId, user.getId())
//                .eq(User::getUsername, user.getUsername()));
        if (collect.contains(user.getUsername())){
            return Result.error(CodeConstant.CODE_400,"用户名已存在");
        }
        return Result.success(userService.saveOrUpdate(user));
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/sysUser/delete/API_003/{id}")
    public Result sysUserDeleteAPI_003(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }

    @ApiOperation("批量删除用户")
    @PostMapping("/sysUser/delete/batch/API_004")
    public Result sysUserDeleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @ApiOperation("导出用户")
    @GetMapping("/sysUser/export/API_005")
    public void sysUserExportAPI_005(HttpServletResponse response,@RequestParam String ids) {
        userService.sysUserExportAPI_005(response,ids);
    }

    @ApiOperation("导入用户")
    @PostMapping("/sysUser/import/API_006")
    public Result sysUserImportAPI_006(MultipartFile file) {
        return Result.success(userService.sysUserImportAPI_006(file));
    }

    @ApiOperation("用户信息导入模板")
    @GetMapping("/sysUser/export/template/API_007")
    public void sysUserExportTemplateAPI_007(HttpServletResponse response) {
        userService.sysUserExportTemplateAPI_007(response);
    }

    @ApiOperation("根据用户id查询用户信息")
    @GetMapping("/sysUser/getById/{id}")
    public Result sysUserGetById(@PathVariable String id) {
        return Result.success(userService.getById(id));
    }

    @ApiOperation("修改密码")
    @PostMapping("/sysUser/update/password")
    public Result sysUserUpdatePassword(@RequestBody PasswordVo passwordVo) {
        userService.sysUserUpdatePassword(passwordVo);
        return Result.success();
    }

}

