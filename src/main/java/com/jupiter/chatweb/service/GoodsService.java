package com.jupiter.chatweb.service;

import com.alibaba.fastjson.JSONArray;
import com.jupiter.chatweb.entity.GoodsEntity;
import com.jupiter.chatweb.pojo.GoodsUpdateDTO;
import com.jupiter.chatweb.util.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface GoodsService {

    AjaxResult<JSONArray> getAllGoods();

    AjaxResult<String> toChat(String username, String merchant, Integer id);

    AjaxResult<GoodsEntity> getGoodsByUsernames(String username1, String username2);

    AjaxResult getSellerGoods(String usertoken, Integer page, Integer size);

    AjaxResult updateGoodsInfo(GoodsUpdateDTO dto,String username);


    AjaxResult uploadGoodsImage(MultipartFile file, Integer goodsId, HttpServletRequest request);

    AjaxResult updateGoodsStatus(Integer id, Integer status, String loginName);

    AjaxResult deleteGoods(Integer id, String loginName);

    AjaxResult createGoods(String loginName, String name, BigDecimal price, String src);
}

