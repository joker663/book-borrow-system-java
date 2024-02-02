package com.lzh.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lzh.entity.User;
import com.lzh.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: token工具类
 * @Author: lzh
 * @Date: 2024-01-27
 */
@Component
public class TokenUtil {

    private static UserService staticUserService;

    @Resource
    private UserService userService;

    // 在SpringBoot项目启动时，从Spring容器中拿到bean对象，并赋给staticUserService静态成员。（因为静态方法不能调用非静态成员变量）
    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成token
     * @param userId
     * @param sign
     * @return
     */
    public static String getToken(String userId, String sign) {
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 4)) // 4小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥（签名）
    }

    /**
     * 获取当前登录的用户信息（可以全局获取，后续调用这个方法，即可获取当前登录用户）
     * @return user对象
     */
    public static User getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
