package com.lzh.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.Menu;
import com.lzh.entity.User;
import com.lzh.exception.MyException;
import com.lzh.mapper.RoleMapper;
import com.lzh.mapper.RoleMenuMapper;
import com.lzh.mapper.UserMapper;
import com.lzh.page.PageData;
import com.lzh.service.MenuService;
import com.lzh.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.utils.TokenUtil;
import com.lzh.vo.PasswordVo;
import com.lzh.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuService menuService;

    @Override
    public UserVo login(UserVo userVo) {
        // 用户密码 md5加密
        // TODO 密码不能明文传输
        List<User> userList = userMapper.selectList(new QueryWrapper<User>()
                .lambda()
                .eq(User::getUsername, userVo.getUsername())
                .eq(User::getPassword, userVo.getPassword()));
        if (userList.size() > 0){
            User user = userList.get(0);
            BeanUtils.copyProperties(user, userVo);
            // 用户登录之后，把token也返回
            String token = TokenUtil.getToken(user.getId().toString(), user.getPassword());
            userVo.setToken(token);

            String role = user.getRole(); // 获取用户的角色（唯一标识，不是id）。ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userVo.setMenus(roleMenus);
            return userVo;
        }else {
            throw new MyException("201","用户名或密码错误");
        }
    }

    @Override
    public User register(UserVo userVo) {
        // 用户密码 md5加密
//        userVo.setPassword(SecureUtil.md5(userVo.getPassword()));
        User user = getUserInfo(userVo);
        if (user == null) {
            user = new User();
            BeanUtils.copyProperties(userVo, user);
            // 默认一个普通用户的角色
//            user.setRole(RoleEnum.ROLE_STUDENT.toString());
            if (user.getNickname() == null) {
                user.setNickname(user.getUsername());
            }
            save(user);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new MyException(CodeConstant.CODE_201, "用户名已存在");
        }
        return user;
    }

    @Override
    public void sysUserUpdatePassword(PasswordVo passwordVo) {
        int update = userMapper.updatePassword(passwordVo);
        if (update < 1){
            throw new MyException(CodeConstant.CODE_201,"密码错误");
        }
    }

    /**
     * 根据username和password查询用户
     * @param userVo
     * @return
     */
    private User getUserInfo(UserVo userVo) {
        List<User> userList = userMapper.selectList(new QueryWrapper<User>()
                .lambda()
                .eq(User::getUsername, userVo.getUsername()));
        User user = null;
        if (userList.size() > 0){
            user = userList.get(0);
        }
        return user;
    }

    @Override
    public Result sysUserListAPI_001(Integer pageNum,Integer pageSize, String username, String phone, String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StrUtil.isNotBlank(username),User::getUsername,username)
                .like(StrUtil.isNotBlank(phone),User::getPhone,phone)
                .like(StrUtil.isNotBlank(nickname),User::getNickname,nickname)
                .orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(pageNum,pageSize);
        userMapper.selectPage(page,queryWrapper);
        return Result.success(new PageData<>(page.getRecords(),page.getTotal()));
    }

    @Override
    public void sysUserExportAPI_005(HttpServletResponse response,String ids) {
        // 部分导出
        LambdaQueryWrapper<User> queryWrapper = null;
        if (ids.length() >0){
            String[] split = ids.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.valueOf(s));
            }
            queryWrapper = new LambdaQueryWrapper<User>().in(idList.size() > 0,User::getId,idList);
        }

        ServletOutputStream out = null;
        // 在内存操作，写出到浏览器
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            List<User> userList = userMapper.selectList(queryWrapper);
            writer.setOnlyAlias(true);

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(userList, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = null;
            fileName = URLEncoder.encode("用户信息", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sysUserExportTemplateAPI_007(HttpServletResponse response) {
        ServletOutputStream out = null;
        // 在内存操作，写出到浏览器
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            List<User> template = userMapper.selectList(new QueryWrapper<User>().lambda().last("limit 2"));

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(template, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = null;
            fileName = URLEncoder.encode("用户信息导入模板", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> sysUserImportAPI_006(MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
            List<User> users = reader.readAll(User.class);
            Set<String> usernameSet1 = users.stream().map(User::getUsername).collect(Collectors.toSet());
            Set<String> usernameSet2 = list().stream().map(User::getUsername).collect(Collectors.toSet());

            // 对导入的数据做个过滤，名称不相同才能导入成功
            Set<String> userSet = usernameSet1.stream()
                    .filter(username -> !usernameSet2.contains(username))
                    .collect(Collectors.toSet());

            int successCount = userSet.size();// 导入成功的数量
            int failedCount = usernameSet1.size() - userSet.size();// 导入失败的数量

            List<User> userList = users.stream().filter(user -> userSet.contains(user.getUsername())).collect(Collectors.toList());
            saveBatch(userList);
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

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        // 根据唯一标识获取角色id
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        // 当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        // 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.sysMenuListAPI_001("");
        // new一个最后筛选完成之后的list（也要组合成树形）
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);// 设置这个用户的一级菜单
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId())); //对menu进行操作，移除不在menuIds中的children子菜单
        }
        return roleMenus;
    }
}
