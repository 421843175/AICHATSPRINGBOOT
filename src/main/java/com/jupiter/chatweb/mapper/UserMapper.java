package com.jupiter.chatweb.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jupiter.chatweb.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-07 16:35:42
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    UserEntity selectByUsername(String username);

    // 自定义SQL：根据用户名查询通道ID
    @Select("SELECT channel FROM user WHERE username = #{username} AND is_active = 1")
    String selectActiveChannelId(@Param("username") String username);

    // 更新通道信息（MyBatis-Plus写法）
    default void updateChannel(String username, String channel) {
        System.out.println("channel====+=="+channel);

        this.update(null,
                new UpdateWrapper<UserEntity>()
                        .set("channel", channel)
                        .set("is_active", 1)
                        .eq("username", username)
//                TODO:
//                        .ne("is_active", 1) // 仅当is_active不等于1时触发更新
        );
    }

    // 清除通道信息
    default void clearChannel(String username) {
        this.update(null,
                new UpdateWrapper<UserEntity>()
                        .set("channel_id", null)
                        .set("is_active", 0)
                        .eq("username", username)
        );
    }
}
