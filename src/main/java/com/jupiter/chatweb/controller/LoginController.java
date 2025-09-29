package com.jupiter.chatweb.controller;


import com.alibaba.fastjson.JSONObject;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.service.UserService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    UserService userService;
    @PostMapping("/register")
    public AjaxResult toRegister(@RequestBody JSONObject userjson){
        UserEntity user = userjson.toJavaObject(UserEntity.class); // 直接转换
        System.out.println("user="+user);
        return userService.register(user);
    }


    @PostMapping("/login")
    public AjaxResult toLogin(@RequestBody JSONObject userjson){
        UserEntity user = userjson.toJavaObject(UserEntity.class); // 直接转换
        System.out.println("user="+user);
        return userService.login(user);
    }

    @PutMapping("/update")
    public AjaxResult<String> updateUser(@RequestBody JSONObject params) {
        String usertoken = params.getString("usertoken");
        String username = TokenUtils.getLoginName(usertoken);
        String oldPassword = params.getString("oldPassword");
        String newPassword = params.getString("newPassword");
        String nick = params.getString("nick");
        String avatar = params.getString("avatar");

        return userService.updateUser(username,nick, avatar, oldPassword, newPassword);
    }

}
