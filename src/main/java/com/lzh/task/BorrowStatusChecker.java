package com.lzh.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzh.entity.Borrow;
import com.lzh.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Description: 定时任务判断图书的借阅状态
 * @Author: lzh
 * @Date: 2024-04-29
 */
@Component
public class BorrowStatusChecker {

    @Autowired
    private BorrowService borrowService;

//    @Scheduled(fixedDelay = 80000) // 30秒执行一次
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨触发
    public void checkBorrowStatus() {
        List<Borrow> borrowList = borrowService.list(new QueryWrapper<Borrow>().lambda().eq(Borrow::getState,0)); // 获取所有借阅中的图书
        for (Borrow borrow : borrowList) {
            if (isBookOverdue(borrow)) {
                borrow.setState(2); // 设置图书状态为已逾期
                borrowService.updateById(borrow); // 更新数据库中图书状态
            }
        }
    }

    private boolean isBookOverdue(Borrow borrow) {
        long passTime = ChronoUnit.DAYS.between(borrow.getBeginTime().toLocalDate(), LocalDate.now()); // 计算借书时间
        return passTime > borrow.getDuringTime(); // 如果借书时间超过了图书的借阅期限，则为逾期
    }

}
