package com.lzh.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lzh.annotation.SysLog;
import com.lzh.common.Result;
import com.lzh.entity.Reader;
import com.lzh.entity.User;
import com.lzh.service.LogService;
import com.lzh.utils.TokenUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Description: AOP日志切面处理器
 * @Author: lzh
 * @Date: 2024-05-01
 */
@Component
@Aspect
public class LogAspectHandler {

    @Autowired
    private LogService logService;

    @Around("@annotation(sysLog)")
    public Object saveLog(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        // 操作内容，也就是注解上面的内容
        String name = sysLog.value();
        // 角色
        String role = sysLog.role();
        //操作时间
        String time = DateUtil.now();
        //操作人
        String username = "";
        User user = TokenUtil.getCurrentUser();
        Reader reader = TokenUtil.getCurrentReader();
        if (ObjectUtil.isNotNull(user)){
            username = user.getUsername();
        }
        if (ObjectUtil.isNotNull(reader)){
            username = reader.getUsername();
        }
        //操作人IP
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getRemoteAddr();

        // 指定具体的接口
        Result result = (Result) joinPoint.proceed();
        Object data = result.getData();
        if (data instanceof User){
            User user1 = (User) data;
            username = user1.getUsername();
        }
        if (data instanceof Reader){
            Reader reader1 = (Reader) data;
            username = reader1.getUsername();
        }

        com.lzh.entity.SysLog sysLog1 = new com.lzh.entity.SysLog();
        sysLog1.setName(name);
        sysLog1.setTime(LocalDateTime.now());
        sysLog1.setUsername(username);
        sysLog1.setIp(ip);
        sysLog1.setRole(role);
        logService.save(sysLog1);

        return result;
    }
}
