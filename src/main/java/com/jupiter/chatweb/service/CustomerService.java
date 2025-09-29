package com.jupiter.chatweb.service;

import com.alibaba.fastjson.JSONArray;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.util.AjaxResult;

public interface CustomerService {
    AjaxResult<JSONArray> getlist();

    AjaxResult getlist(Integer id, UserEntity user);

}
