package com.jupiter.chatweb.controller;


import com.jupiter.chatweb.entity.EvaluateEntity;
import com.jupiter.chatweb.service.EvaluateService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
//商品模块
@RequestMapping("/evaluate")
@CrossOrigin("*")
public class EvaluateController {

    @Autowired
    EvaluateService evaluateService;

    @PostMapping("/submit")
    public AjaxResult<String> submitEvaluate(@RequestBody EvaluateEntity request) {
        // 验证参数
        if (request.getScore() == null || request.getScore() < 0 || request.getScore() > 5) {
            return AjaxResult.error("非法评分值");
        }

        return evaluateService.savething(request);
    }

    @GetMapping("/list")
    public AjaxResult listReplies(@RequestParam String usertoken) {
        String merchant = TokenUtils.getLoginName(usertoken);
        return AjaxResult.success(evaluateService.getRepliesByMerchant(merchant));
    }
}
