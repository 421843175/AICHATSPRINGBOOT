package com.jupiter.chatweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.chatweb.entity.ReplyEntity;
import com.jupiter.chatweb.service.ReplyService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//商品模块
@RequestMapping("/reply")
@CrossOrigin("*")
public class ReplyController {
//    回复控制器 用于机器人回复 自动回复等

    @Autowired
    private ReplyService replyService;

    @GetMapping("/suggestions")
    public AjaxResult<List<ReplyEntity>> getSuggestions(
            @RequestParam String merchant,
            @RequestParam(required = false) Integer goodsId) {

        List<ReplyEntity> suggestions = replyService.getSuggestions(merchant, goodsId);
        return AjaxResult.success(suggestions);
    }

    // ReplyController.java 新增接口
    @GetMapping("/answer")
    public AjaxResult<String> getAnswer(
            @RequestParam String question,
            @RequestParam String merchant,
            @RequestParam String usertoken) {
        String sender = TokenUtils.getLoginName(usertoken);
        ReplyEntity reply = replyService.findByQuestionAndMerchant(question, merchant,sender);
        return reply != null ?
                AjaxResult.success(reply.getAnswer()) :
                AjaxResult.error("未找到相关答案");
    }


    // 获取商家所有问答
    @GetMapping("/list")
    public AjaxResult listReplies(@RequestParam String usertoken) {
        String merchant = TokenUtils.getLoginName(usertoken);
        return AjaxResult.success(replyService.getRepliesByMerchant(merchant));
    }

    // 新增/修改问答
    @PostMapping("/save")
    public AjaxResult saveReply(@RequestBody JSONObject edit) {
        ReplyEntity replyEntity = new ReplyEntity();

        replyEntity.setAnswer(edit.getString("answer"));
        replyEntity.setPrority(edit.getInteger("prority"));
        replyEntity.setId(edit.getInteger("id"));
        replyEntity.setQuestion(edit.getString("question"));

        String merchant = TokenUtils.getLoginName(edit.getString("usertoken"));
        replyEntity.setReceiver(merchant);
        return replyService.saveReply(replyEntity);
    }

    // 删除问答
    @GetMapping("/delete")
    public AjaxResult deleteReply(@RequestParam Integer id,
                                  @RequestParam String usertoken) {
        String merchant = TokenUtils.getLoginName(usertoken);
        return replyService.deleteReply(id, merchant);
    }


}
