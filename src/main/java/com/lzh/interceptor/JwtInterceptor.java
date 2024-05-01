package com.lzh.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lzh.annotation.AuthAccess;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.User;
import com.lzh.exception.MyException;
import com.lzh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: Jwt拦截器，对请求携带的token进行验证
 * @Author: lzh
 * @Date: 2024-01-27
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }else {
            // 映射到方法上的话，就看方法上加没加这个注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AuthAccess authAccess = handlerMethod.getMethodAnnotation(AuthAccess.class);
            if (authAccess != null){// 加了AuthAccess允许访问注解的话，也允许访问
                return true;
            }
        }
        if (StrUtil.isBlank(token)) {
            throw new MyException(CodeConstant.CODE_401, "无token，请重新登录");
        }
        // 获取 token 中的 user id
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new MyException(CodeConstant.CODE_401, "token验证失败，请重新登录");
        }
        // 根据token中的userid查询数据库
        User user = userService.getById(userId);
        if (user == null) {
            throw new MyException(CodeConstant.CODE_401, "用户不存在，请重新登录");// 防止假token
        }
        // 用户密码加签验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e) {
            throw new MyException(CodeConstant.CODE_401, "token验证失败，请重新登录");
        }
        return true;
    }
}
