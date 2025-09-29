package com.jupiter.chatweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.service.CustomerService;
import com.jupiter.chatweb.service.UserService;
import com.jupiter.chatweb.service.impl.CustomerServiceImpl;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//管理员模块
@RequestMapping("/customer")
@CrossOrigin("*")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;



    // 获取用户列表（仅管理员）
    @GetMapping("/list")
    public AjaxResult<JSONArray> listUsers(String usertoken) {
        // 权限验证
        String loginName = TokenUtils.getLoginName(usertoken);
        UserEntity currentUser = userService.getByUsername(loginName);
        if (currentUser == null || currentUser.getRole() != 0) {
            return AjaxResult.error("无权限访问");
        }
        return customerService.getlist();

    }

    // 删除用户（仅管理员）
    @PostMapping("/delete")
    public AjaxResult deleteUser(@RequestBody JSONObject json) {
        try {
            Integer id = json.getInteger("id");
            String usertoken = json.getString("usertoken");

            // 权限验证
            String loginName = TokenUtils.getLoginName(usertoken);
            UserEntity currentUser = userService.getByUsername(loginName);
            return customerService.getlist(id,currentUser);

        } catch (Exception e) {
            return AjaxResult.error("参数错误：" + e.getMessage());
        }
    }
}
