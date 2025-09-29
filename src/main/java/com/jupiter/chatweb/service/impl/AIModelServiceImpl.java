package com.jupiter.chatweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jupiter.chatweb.entity.AIModel;
import com.jupiter.chatweb.mapper.AIModelMapper;
import com.jupiter.chatweb.service.AIModelService;
import com.jupiter.chatweb.util.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AIModelServiceImpl extends ServiceImpl<AIModelMapper, AIModel> implements AIModelService {

    @Override
    public AjaxResult<String> saveModel(String username, String gender, String layers) {
        LambdaUpdateWrapper<AIModel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AIModel::getUsername, username)
                .eq(AIModel::getGender, gender);

        AIModel model = new AIModel();
        model.setUsername(username);
        model.setGender(gender);
        model.setLayers(layers);
        model.setUpdateTime(new Date());

        this.saveOrUpdate(model, wrapper);
        return AjaxResult.success("保存成功");
    }

    @Override
    public AjaxResult<AIModel> getByUserAndGender(String username) {
        AjaxResult<AIModel> success = AjaxResult.success(this.lambdaQuery()
                .eq(AIModel::getUsername, username)
                .one());
        return success;

    }
}