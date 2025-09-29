package com.jupiter.chatweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.chatweb.entity.GoodsEntity;
import com.jupiter.chatweb.pojo.GoodsUpdateDTO;
import com.jupiter.chatweb.service.GoodsService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
//商品模块
@RequestMapping("/goods")
@CrossOrigin("*")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @GetMapping("/list")
    public AjaxResult<JSONArray> getList(){
        return goodsService.getAllGoods();
    }

    @PostMapping("/toChat")
    public AjaxResult<String> toChat(@RequestBody JSONObject toChatJson){
        System.out.println("toChatJSON="+toChatJson);

        String usertoken = toChatJson.getString("usertoken");
        String username = TokenUtils.getLoginName(usertoken);

        JSONObject item = toChatJson.getJSONObject("item");
        Integer id = item.getInteger("id");
        String merchant = item.getString("merchant");


        return goodsService.toChat(username,merchant,id);
    }

    @GetMapping("/consultant")
    public AjaxResult<GoodsEntity> getGoodsByFriendship(
            String usertoken,
          String username2) {
        String username1 = TokenUtils.getLoginName(usertoken);
        return goodsService.getGoodsByUsernames(username1, username2);
    }

    // 获取卖家商品列表
    @GetMapping("/seller")
    public AjaxResult getSellerGoods(
            @RequestParam String usertoken,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return goodsService.getSellerGoods(usertoken, page, size);
    }

    // 更新商品信息
    @PostMapping("/update")
    public AjaxResult updateGoods(@RequestBody String jsonStr) {
        JSONObject json = JSONObject.parseObject(jsonStr);

        // 获取字段
        Integer id = json.getInteger("id");
        String usertoken = json.getString("usertoken");
        String name = json.getString("name");
        BigDecimal price = json.getBigDecimal("price");
        String src = json.getString("src");



//        自己封装
        GoodsUpdateDTO goodsUpdateDTO = new GoodsUpdateDTO();
        goodsUpdateDTO.setId(id);
        goodsUpdateDTO.setName(name);
        goodsUpdateDTO.setPrice(price);
        goodsUpdateDTO.setSrc(src);

        String merchant = TokenUtils.getLoginName(usertoken);
        return goodsService.updateGoodsInfo(goodsUpdateDTO,merchant);

    }

    // GoodsController.java
    @PostMapping("/status")
    public AjaxResult updateGoodsStatus(@RequestBody JSONObject json) {
        try {
            // 解析参数
            Integer id = json.getInteger("id");
            Integer status = json.getInteger("status");
            String usertoken = json.getString("usertoken");

            String loginName = TokenUtils.getLoginName(usertoken);

            // 业务处理
            return goodsService.updateGoodsStatus(id, status, loginName);

        } catch (Exception e) {
            return AjaxResult.error("参数解析失败: " + e.getMessage());
        }
    }




    // 删除商品
    @PostMapping("/delete")
    public AjaxResult deleteGoods(@RequestBody JSONObject json) {
        try {
            Integer id = json.getInteger("id");
            String usertoken = json.getString("usertoken");

            return goodsService.deleteGoods(
                    id,
                    TokenUtils.getLoginName(usertoken)
            );

        } catch (Exception e) {
            return AjaxResult.error("参数解析失败: " + e.getMessage());
        }
    }

    // 新增商品
    @PostMapping("/create")
    public AjaxResult createGoods(@RequestBody JSONObject json) {
        try {
            // 参数校验
            if (!json.containsKey("usertoken")) {
                return AjaxResult.error("缺少用户凭证");
            }
            if (!json.containsKey("name")) {
                return AjaxResult.error("缺少商品名称");
            }
            if (!json.containsKey("price")) {
                return AjaxResult.error("缺少商品价格");
            }

            // 解析参数
            String usertoken = json.getString("usertoken");
            String name = json.getString("name");
            BigDecimal price = json.getBigDecimal("price");
            String src = json.getString("src"); // 可空

            return goodsService.createGoods(
                    TokenUtils.getLoginName(usertoken),
                    name,
                    price,
                    src
            );

        } catch (Exception e) {
            return AjaxResult.error("参数解析失败: " + e.getMessage());
        }
    }
}
