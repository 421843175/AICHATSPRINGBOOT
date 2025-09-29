package com.jupiter.chatweb.service;

import com.jupiter.chatweb.entity.EvaluateEntity;
import com.jupiter.chatweb.util.AjaxResult;

public interface EvaluateService {
    AjaxResult<String> savething(EvaluateEntity request);

    Object getRepliesByMerchant(String merchant);

}
