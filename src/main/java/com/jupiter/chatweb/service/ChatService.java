package com.jupiter.chatweb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.chatweb.entity.ReplyEntity;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.util.AjaxResult;

import java.util.List;

public interface ChatService {
//    AjaxResult<Integer> connect(String username);

    AjaxResult<JSONArray> getFriends(String username,Integer roleid);
    AjaxResult<JSONArray> getHistory(String sender, String receiver);

     AjaxResult<String> clearUnRead(String sender, String receiver);

    AjaxResult<String> updateUnread(String username, String receiver);

    AjaxResult<JSONArray> processRobotMessage(String username, String receiver, String content);


    AjaxResult<UserEntity> getMy(String username);

     boolean existsFriendship(String user1, String user2);

//    List<ReplyEntity> getRandomSuggestions(String receiver, int count);

    void saveMessage(String sender, String receiver, String content);

    AjaxResult<String> toArtificial(String username, String merchant);


    AjaxResult<List<JSONObject>> searchFriends(String username, String keyword);

    AjaxResult<String> toChatRobot(String username);

}
