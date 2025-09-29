package com.jupiter.chatweb.service;

import com.jupiter.chatweb.entity.ReplyEntity;
import com.jupiter.chatweb.util.AjaxResult;

import java.util.List;

public interface ReplyService {
    List<ReplyEntity> getSuggestions(String merchant, Integer goodsId);

    ReplyEntity findByQuestionAndMerchant(String question, String merchant,String user);

    Object getRepliesByMerchant(String merchant);

    AjaxResult saveReply(ReplyEntity entity);

    AjaxResult deleteReply(Integer id, String merchant);
}
