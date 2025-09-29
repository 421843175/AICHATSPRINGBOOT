package com.jupiter.chatweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.mapper.UserMapper;
import com.jupiter.chatweb.service.CustomerService;
import com.jupiter.chatweb.util.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements CustomerService {

    @Override
    public AjaxResult<JSONArray> getlist() {
        // 查询用户列表（排除密码）
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "nick", "role", "logintime", "head", "perms");
        List<UserEntity> users = this.list(queryWrapper);

        // 构造返回数据
        JSONArray data = new JSONArray();
        for (UserEntity user : users) {
            JSONObject obj = new JSONObject();
            obj.put("id", user.getId());
            obj.put("username", user.getUsername());
            obj.put("nick", user.getNick());
            obj.put("role", user.getRole());
            obj.put("logintime", user.getLogintime());
            obj.put("head", user.getHead());
            obj.put("perms", user.getPerms());
            data.add(obj);
        }
        return AjaxResult.success(data);
    }

    @Override
    public AjaxResult getlist(Integer id, UserEntity currentUser) {

        if (currentUser == null || currentUser.getRole() != 0) {
            return AjaxResult.error("无权限操作");
        }

        // 执行删除
        boolean success = this.removeById(id);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("用户不存在");
    }
}
