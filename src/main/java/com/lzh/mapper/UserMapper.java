package com.lzh.mapper;

import com.lzh.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzh.vo.PasswordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lizhihao
 * @since 2024-01-21
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(PasswordVo passwordVo);
}
