package com.lzh.mapper;

import com.lzh.entity.Reader;
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
 * @since 2024-02-02
 */
@Mapper
public interface ReaderMapper extends BaseMapper<Reader> {

    @Update("update t_reader set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(PasswordVo passwordVo);

}
