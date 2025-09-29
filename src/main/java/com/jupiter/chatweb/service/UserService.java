package com.jupiter.chatweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.util.AjaxResult;


import java.util.Map;

/**
 * 
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-07 16:35:42
 */
public interface UserService extends IService<UserEntity> {
    UserEntity selectone(String username);
    AjaxResult register(UserEntity user);

    AjaxResult login(UserEntity user);

     UserEntity getByUsername(String username);


    AjaxResult<String> updateUser(String username,String nick, String avatar, String oldPassword, String newPassword);
//    PageUtils queryPage(Map<String, Object> params);
}

