package com.jupiter.chatweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.chatweb.entity.ReplyEntity;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.service.ChatService;
import com.jupiter.chatweb.service.UserService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.message.AuthException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
//聊天模块
@RequestMapping("/chat")
@CrossOrigin("*")
public class ChatContorller {

@Autowired
    ChatService chatService;

//    @GetMapping("/connect")
//    public AjaxResult<Integer>  connect( String usertoken){
//        System.out.println("the usertoken="+usertoken);
//
//        String username = TokenUtils.getLoginName(usertoken);
//        return chatService.connect(username);
//    }

    @GetMapping("/getFriend")
    //如果是1是对接卖家(机器人/人工) 如果是0是对接客服(机器人/人工)
    //  1的情况查1或2 0的时候查0
    public AjaxResult<JSONArray> getFriends(String usertoken,Integer roleid) {
        try {
            String username = TokenUtils.getLoginName(usertoken);
            return chatService.getFriends(username,roleid);
        }catch (Exception e) {
            return AjaxResult.error("获取好友列表失败：" + e.getMessage());
        }
    }
    @GetMapping("/getMy")
    public AjaxResult<UserEntity> getMy(String usertoken){
        System.out.println("the getFriends="+usertoken);

        String username = TokenUtils.getLoginName(usertoken);

        return chatService.getMy(username);
    }







    @GetMapping("/getHistory")
    public AjaxResult<JSONArray> getHistory(String usertoken,String receiver) {

        try {
            String sender = TokenUtils.getLoginName(usertoken);
            return chatService.getHistory(sender, receiver);
        } catch (Exception e) {
            return AjaxResult.error("获取历史记录失败：" + e.getMessage());
        }
    }


    @GetMapping("/clearUnRead")
    public AjaxResult<String> clearUnRead(String usertoken,String receiver) {

        // 从token解析出发送者（需要实现token解析逻辑）
        String username = TokenUtils.getLoginName(usertoken);
        return chatService.clearUnRead(username, receiver);
    }

    @PostMapping("/updateUnread")
    public AjaxResult<String> updateUnread(
            String usertoken,String receiver) {

        // 从token解析出发送者（需要实现token解析逻辑）
        String username = TokenUtils.getLoginName(usertoken);
       return chatService.updateUnread(username,receiver);

    }

    @PostMapping("/robot")
    public AjaxResult<JSONArray> handleRobotMessage(
            @RequestBody JSONObject robot) {
        try {
            System.out.println("你好robot:"+robot);
        String usertoken = robot.getString("usertoken");
        String receiver = robot.getString("receiver");
        String content = robot.getString("content");

            String username = TokenUtils.getLoginName(usertoken);

            // 参数校验
            if (StringUtils.isEmpty(receiver) || StringUtils.isEmpty(content)) {
                return AjaxResult.error("参数不完整");
            }
            return chatService.processRobotMessage(username, receiver, content);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("服务暂不可用");
        }
    }

//    @GetMapping("/suggestions")
//    public AjaxResult<List<ReplyEntity>> getSuggestions(
//            @RequestParam String receiver,
//            @RequestParam(defaultValue = "4") int count
//    ) {
//        List<ReplyEntity> suggestions = chatService.getRandomSuggestions(receiver, count);
//        return AjaxResult.success(suggestions);
//    }

    //转人工
    @GetMapping("/toArtificial")
    public AjaxResult<String> toArtificial(String usertoken,String merchant){
        String username = TokenUtils.getLoginName(usertoken);
        return chatService.toArtificial(username,merchant);
    }

    //转人工
    @GetMapping("/toChatRobot")
    public AjaxResult<String> toChatRobot(String usertoken){
        String username = TokenUtils.getLoginName(usertoken);
        return chatService.toChatRobot(username);
    }


//    搜索最近联系人
// 新增联系人搜索接口
@GetMapping("/searchFriend")
public AjaxResult<List<JSONObject>> searchFriends(
        @RequestParam String keyword,
        @RequestParam String usertoken
) {
    try {
        String username = TokenUtils.getLoginName(usertoken);
        return chatService.searchFriends(username, keyword);
    } catch (Exception e) {
        return AjaxResult.error("搜索失败: " + e.getMessage());
    }
}
}
