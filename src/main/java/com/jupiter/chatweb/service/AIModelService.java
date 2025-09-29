package com.jupiter.chatweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jupiter.chatweb.entity.AIModel;
import com.jupiter.chatweb.util.AjaxResult;

import java.util.List;

public interface AIModelService extends IService<AIModel> {
    /**
     * 保存/更新配置
     */
    AjaxResult saveModel(String userId, String gender, String layers);

    /**
     * 获取用户配置
     */
    AjaxResult<AIModel> getByUserAndGender(String userId);
}