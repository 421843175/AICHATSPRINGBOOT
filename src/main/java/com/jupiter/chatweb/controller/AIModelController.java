package com.jupiter.chatweb.controller;


import com.jupiter.chatweb.entity.AIModel;
import com.jupiter.chatweb.pojo.ModelRequest;
import com.jupiter.chatweb.service.AIModelService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//人物形象模块
@RequestMapping("/model")
@CrossOrigin("*")
public class AIModelController {


    @Autowired
    AIModelService aiModelService;

    @PostMapping("/save")
    public AjaxResult<String> toSave(@RequestBody ModelRequest request){
        String loginName = TokenUtils.getLoginName(request.getUsertoken());
        return aiModelService.saveModel(loginName,request.getGender(),request.getLayers());
    }


    @PostMapping("/getModel")
    public AjaxResult<AIModel> toGetModel(@RequestBody String usertoken){
        String loginName = TokenUtils.getLoginName(usertoken);
        return aiModelService.getByUserAndGender(loginName);
    }

    @GetMapping("/getMerchantModel")
    public AjaxResult<AIModel> toGetMerchantModel(String merchant){
        return aiModelService.getByUserAndGender(merchant);
    }


}
