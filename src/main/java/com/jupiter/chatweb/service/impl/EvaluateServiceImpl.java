package com.jupiter.chatweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jupiter.chatweb.entity.EvaluateEntity;
import com.jupiter.chatweb.entity.ReplyEntity;
import com.jupiter.chatweb.mapper.EvaluateDao;
import com.jupiter.chatweb.service.EvaluateService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EvaluateServiceImpl extends ServiceImpl<EvaluateDao,EvaluateEntity> implements EvaluateService {
   @Autowired
   EvaluateDao evaluateDao;

    @Override
    public AjaxResult<String> savething(EvaluateEntity request) {
        // 创建评价实体
        EvaluateEntity evaluate = new EvaluateEntity();
        evaluate.setMerchant(request.getMerchant());
        evaluate.setUsername(TokenUtils.getLoginName(request.getUsername()));
        evaluate.setScore(request.getScore());
        evaluate.setContent(request.getContent());
        evaluate.setCreateTime(new Date());

        if(evaluate.getUsername().equals(evaluate.getMerchant()))
            return AjaxResult.error("您不能自己给自己评分");
        // 保存到数据库
        this.saveOrUpdate(evaluate);

        return AjaxResult.success("评价提交成功");
    }

    @Override
    public List<EvaluateEntity> getRepliesByMerchant(String merchant) {
        return evaluateDao.selectList(
                new QueryWrapper<EvaluateEntity>()
                        .eq("merchant", merchant)
                        .orderByDesc("create_time")
        );
    }

}
