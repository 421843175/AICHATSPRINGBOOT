package com.jupiter.chatweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.jupiter.chatweb.entity.*;
import com.jupiter.chatweb.mapper.*;
import com.jupiter.chatweb.service.ChatService;
import com.jupiter.chatweb.service.DeepSeekService;
import com.jupiter.chatweb.service.ReplyService;
import com.jupiter.chatweb.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

//
//    @Override
//    public AjaxResult<Integer> connect(String username) {
//        return null;
//    }

    @Autowired
    FriendshipsDao friendshipsDao;

    @Autowired
    MessagesDao messagesDao;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ReplyDao replyDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    @Lazy  //防止循环依赖
    ReplyService replyService;

    @Autowired
    DeepSeekService deepSeekService;



    @Override
    public AjaxResult<JSONArray> getFriends(String username,Integer roleid) {

        try {
            System.out.println("username====>"+username);
            // 查询用户的所有好友关系
            List<FriendshipsEntity> friendships = friendshipsDao.selectList(
                    new QueryWrapper<FriendshipsEntity>()
                            .eq("username1", username)
                            .or()
                            .eq("username2", username)

            );

            JSONArray result = new JSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            for (FriendshipsEntity friendship : friendships) {
                JSONObject friend = new JSONObject();

                // 判断当前用户是username1还是username2
                if (username.equals(friendship.getUsername1())) {
                    // user2
                    if(friendship.getIsRole2()==null )continue;
                    if(friendship.getIsRole2()==1 || friendship.getIsRole2()==2)
                            friendship.setIsRole2(1);

                    if(!Objects.equals(friendship.getIsRole2(), roleid)) continue;
                        friend.put("username", friendship.getUsername2());
                        friend.put("head", friendship.getUsername2head());
//                    A不能得到B没读的条数 只能得到自己没读的条数
                        friend.put("unreadnum",friendship.getUser1unreadnum());


//                        获取user2的nickname
                    UserEntity userEntity = userMapper.selectOne(
                            new QueryWrapper<UserEntity>().eq("username", friendship.getUsername2()));
                    if(userEntity==null) friend.put("nickname","用户已注销");
                    else friend.put("nickname",userEntity.getNick());

                } else {
                    if(friendship.getIsRole1()==null )continue;
                    if(friendship.getIsRole1()!=null||friendship.getIsRole1()==1 || friendship.getIsRole1()==2)
                        friendship.setIsRole1(1);

                    if(!Objects.equals(friendship.getIsRole1(), roleid))  continue;
                        friend.put("username", friendship.getUsername1());
                        friend.put("head", friendship.getUsername1head());
                        friend.put("unreadnum", friendship.getUser2unreadnum());


                    //                        获取user1的nickname
                    UserEntity userEntity = userMapper.selectOne(
                            new QueryWrapper<UserEntity>().eq("username", friendship.getUsername1()));
                    if(userEntity==null) friend.put("nickname","用户已注销");
                    else friend.put("nickname",userEntity.getNick());

                }

                // 设置消息相关信息
                if(friendship.getEndtime()!=null)
                friend.put("timestamp", sdf.format(friendship.getEndtime()));
                else friend.put("timestamp", null);
                friend.put("messageEnd", friendship.getEndmessage());

                //是否是机器人
                friend.put("isRobot", friendship.getIsRobot());

                //相关商品信息
                GoodsEntity goodsEntity = goodsDao.selectById(friendship.getGoodsId());
                if(goodsEntity!=null){
                    friend.put("good_name",goodsEntity.getName());
                    friend.put("good_src",goodsEntity.getSrc());
                    friend.put("good_price",goodsEntity.getPrice());
                }

                result.add(friend);
            }

            return AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取好友列表失败：" + e.getMessage());
        }
    }


    @Override
    public AjaxResult<JSONArray> getHistory(String sender, String receiver) {
        try {
            QueryWrapper<MessagesEntity> queryWrapper = new QueryWrapper<>();
            // 查询双方之间的所有消息
            queryWrapper.and(wrapper -> wrapper
                            .eq("sender_username", sender)
                            .eq("receiver_username", receiver)
                    )
                    .or(wrapper -> wrapper
                            .eq("sender_username", receiver)
                            .eq("receiver_username", sender)
                    )
                    .orderByAsc("send_time")
                    .orderByAsc("id");// 按时间升序排列

            List<MessagesEntity> messages = messagesDao.selectList(queryWrapper);

            JSONArray result = new JSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (MessagesEntity message : messages) {
                JSONObject msg = new JSONObject();
                msg.put("sender", message.getSenderUsername());
                msg.put("content", message.getContent());
                msg.put("time", sdf.format(message.getSendTime()));
                msg.put("isSelf", message.getSenderUsername().equals(sender));
                result.add(msg);
            }

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取历史记录失败：" + e.getMessage());
        }
    }

    @Override
    public AjaxResult<String> clearUnRead(String sender, String receiver) {
        FriendshipsEntity friendshipsEntity = friendshipsDao.selectOne
                (new QueryWrapper<FriendshipsEntity>()
                .eq("username1", sender)
                .eq("username2", receiver));
        if(friendshipsEntity==null){
            friendshipsEntity = friendshipsDao.selectOne
                    (new QueryWrapper<FriendshipsEntity>()
                            .eq("username1", receiver)
                            .eq("username2", sender));
            if(friendshipsEntity==null)
            return AjaxResult.error("错误的联系关系");
        }
        // 判断当前用户是username1还是username2
        if (sender.equals(friendshipsEntity.getUsername1())){
            friendshipsEntity.setUser1unreadnum(0);
        }else {
            friendshipsEntity.setUser2unreadnum(0);
        }
        friendshipsDao.updateById(friendshipsEntity);
        return AjaxResult.success("清除成功");
    }


   public AjaxResult<String> updateUnread(String username, String receiver){
       friendshipsDao.incrementUnreadCount(username, receiver);
       return AjaxResult.success("未读信息更新成功");
    }

//    TODO: 机器人回复
    @Override
    public AjaxResult<JSONArray> processRobotMessage(String sender, String receiver, String content) {
        try {
            // 1. 参数校验
            if (StringUtils.isEmpty(sender) || StringUtils.isEmpty(receiver)) {
                return AjaxResult.error("用户身份验证失败");
            }
            if (StringUtils.isEmpty(content) || content.length() > 500) {
                return AjaxResult.error("消息内容无效（1-500字符）");
            }

            // 2. 验证机器人关系
            FriendshipsEntity relationship = friendshipsDao.selectOne(
                    new QueryWrapper<FriendshipsEntity>()
                            .and(wrapper -> wrapper
                                    .eq("username1", sender)
                                    .eq("username2", receiver)
                                    .or()
                                    .eq("username1", receiver)
                                    .eq("username2", sender))
                            .eq("is_robot", 1)
            );
            if (relationship == null) {
                return AjaxResult.error("非机器人会话");
            }

            // 3. 保存消息
            JSONArray resultArray = new JSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            // 用户消息
            MessagesEntity userMessage = new MessagesEntity();
            userMessage.setSenderUsername(sender);
            userMessage.setReceiverUsername(receiver);
            userMessage.setContent(content);
            userMessage.setSendTime(new Date());
            messagesDao.insert(userMessage);
            resultArray.add(buildMessageJson(userMessage, sdf, true));

            // 机器人回复


//             首先看库里面有没有

            ReplyEntity byQuestionAndMerchant = replyService.findByQuestionAndMerchant(content, receiver, sender);

            MessagesEntity botReply = new MessagesEntity();
            botReply.setSenderUsername(receiver);
            botReply.setReceiverUsername(sender);
            botReply.setSendTime(new Date());
            if(byQuestionAndMerchant!=null){
                //库里面有一摸一样的
                botReply.setContent(byQuestionAndMerchant.getAnswer()+"("+byQuestionAndMerchant.getQuestion()+")");
            }else {
            //预设内容
                QueryWrapper<ReplyEntity> receiverQ = new QueryWrapper<ReplyEntity>().eq("receiver", receiver);
                List<ReplyEntity> replyEntities = replyDao.selectList(receiverQ);


                //库里面找不到类似的  看看以前和人工有没有历史记录聊过类似的话题
                // 并且交给AI进行整合
                QueryWrapper<MessagesEntity> receiver_user = new QueryWrapper<MessagesEntity>().eq("receiver_username", receiver);
                List<MessagesEntity> messagesEntities = messagesDao.selectList(receiver_user);
                System.out.println("历史记录:"+messagesEntities);

                //整合：
                String code="预设内容:"+replyEntities+"\n"+"历史记录"+messagesEntities;
                String str = getChatAI("你来充当一个商铺客服 根据人工和别人的历史记录来回复这个新的用户的商品问题 如果没有合适的 委婉回复一下\n"+code,
                        content);
                botReply.setContent(str);
            }
            resultArray.add(buildMessageJson(botReply, sdf, false));
            // 4. 更新会话状态
            messagesDao.insert(botReply);
            friendshipsDao.updateLastMessage(sender, receiver, botReply.getContent());


//             TODO: 增加情绪判定  情绪库
            String emotion="['默认','愤怒','难过','生气','思考','笑1','笑2'，'笑3','震惊','自信']";
            String getemotion = getChatAI("判定一下用户发送这句话的情绪 只需要返回情绪库中的某个情绪就可以 如果情绪库与该语句匹配情绪重复返回任意一个 主要只返回一个不应该有或:\n情绪库:"+emotion,
                    content);
            resultArray.add(getemotion);

            return AjaxResult.success(resultArray);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("消息处理失败：" + e.getMessage());
        }
    }

    public String getChatAI(String role,String sender){
        List<DeepSeekService.ChatMessage> messages = Arrays.asList(
                new DeepSeekService.ChatMessage("system", role),

                new DeepSeekService.ChatMessage("user", sender)
        );
        return deepSeekService.chatCompletion(messages);
    }

    @Override
    public AjaxResult<UserEntity> getMy(String username) {
        QueryWrapper<UserEntity> u = new QueryWrapper<UserEntity>().eq("username", username);
        UserEntity userEntity = userMapper.selectOne(u);
        if(userEntity!=null){
            return AjaxResult.success(userEntity);
        }
        return AjaxResult.error("未查询到用户信息");
    }


    private JSONObject buildMessageJson(MessagesEntity message, SimpleDateFormat sdf, boolean isSelf) {
        return new JSONObject()
                .fluentPut("id", message.getId())
                .fluentPut("sender", message.getSenderUsername())
                .fluentPut("content", message.getContent())
                .fluentPut("time", sdf.format(message.getSendTime()))
                .fluentPut("isSelf", isSelf);
    }

    public boolean existsFriendship(String user1, String user2) {
        return friendshipsDao.selectCount(new LambdaQueryWrapper<FriendshipsEntity>()
                .eq(FriendshipsEntity::getUsername1, user1)
                .eq(FriendshipsEntity::getUsername2, user2)) > 0;
    }

//    public List<ReplyEntity> getRandomSuggestions(String receiver, int count) {
//        List<ReplyEntity> all = replyDao.selectList(
//                new QueryWrapper<ReplyEntity>()
//                        .eq("receiver", receiver)
//                        .orderByDesc("prority")
//        );
//        Collections.shuffle(all);
//        return all.subList(0, Math.min(count, all.size()));
//    }

    public void saveMessage(String sender, String receiver, String content) {
        MessagesEntity message = new MessagesEntity();
        message.setSenderUsername(sender);
        message.setReceiverUsername(receiver);
        message.setContent(content);
        message.setSendTime(new Date());
        message.setIsSend(1);       // 标记为已发送
        message.setDeleteStatus(0);  // 未删除状态

        messagesDao.insert(message);
    }

    @Override
    public AjaxResult<String> toArtificial(String username, String merchant) {

//        QueryWrapper<FriendshipsEntity> eq = new QueryWrapper<FriendshipsEntity>().eq("username1", username).eq("username2", merchant)
//                .or().eq("username1", merchant).eq("username2", username);
//
        QueryWrapper<FriendshipsEntity> eq = new QueryWrapper<FriendshipsEntity>()
                .nested(wq -> wq
                        .eq("username1", username)
                        .eq("username2", merchant)
                )
                .or()
                .nested(wq -> wq
                        .eq("username1", merchant)
                        .eq("username2", username)
                );

        FriendshipsEntity friendshipsEntity = friendshipsDao.selectOne(eq);
        if(friendshipsEntity!=null){
            //设置is_robot是0 进入人工逻辑
            friendshipsEntity.setIsRobot(0);
            friendshipsDao.updateById(friendshipsEntity);
            return AjaxResult.success("转人工成功");
        }
        return AjaxResult.error("严重错误 好有关系不存在");
    }


    //去机器人
    @Override
    public AjaxResult<String> toChatRobot(String username) {

//        QueryWrapper<FriendshipsEntity> eq = new QueryWrapper<FriendshipsEntity>().eq("username1", username).eq("username2", merchant)
//                .or().eq("username1", merchant).eq("username2", username);
//
        QueryWrapper<FriendshipsEntity> eq = new QueryWrapper<FriendshipsEntity>()
                .nested(wq -> wq
                        .eq("username1", username)
                )
                .or()
                .nested(wq -> wq
                        .eq("username2", username)
                );

        List<FriendshipsEntity> friendshipsEntities = friendshipsDao.selectList(eq);
     friendshipsEntities.forEach(friendships -> {
         if(friendships!=null){
             //设置is_robot是0 进入人工逻辑
             friendships.setIsRobot(1);
             friendshipsDao.updateById(friendships);

         }

     });
        return AjaxResult.success("转人工成功");
    }

    @Override
    public AjaxResult<List<JSONObject>> searchFriends(String username, String keyword) {
        // 1. 根据昵称查找相关用户
        List<UserEntity> nickUsers = userMapper.selectList(
                new QueryWrapper<UserEntity>().like("nick", keyword)
        );
        List<String> nickUsernames = nickUsers.stream()
                .map(UserEntity::getUsername)
                .collect(Collectors.toList());

        // 2. 构建复合查询条件
        QueryWrapper<FriendshipsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                        .like("username1", keyword)
                        .or()
                        .like("username2", keyword)
                        .or()
                        .in(nickUsernames.size() > 0, "username1", nickUsernames)
                        .or()
                        .in(nickUsernames.size() > 0, "username2", nickUsernames)
                )
                .and(wrapper -> wrapper
                        .eq("username1", username)
                        .or()
                        .eq("username2", username)
                )
                .orderByDesc("endTime");

        // 3. 执行查询并处理结果
        List<FriendshipsEntity> friendships = friendshipsDao.selectList(queryWrapper);

        List<JSONObject> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        for (FriendshipsEntity friendship : friendships) {
            boolean isUser1 = username.equals(friendship.getUsername1());
            String friendUsername = isUser1 ? friendship.getUsername2() : friendship.getUsername1();

            // 获取好友昵称
            UserEntity friendUser = userMapper.selectOne(
                    new QueryWrapper<UserEntity>().eq("username", friendUsername)
            );

            JSONObject friend = new JSONObject();
            friend.put("username", friendUsername);
            friend.put("nickname", friendUser != null ? friendUser.getNick() : "用户已注销");
            friend.put("head", isUser1 ? friendship.getUsername2head() : friendship.getUsername1head());
            friend.put("unreadnum", isUser1 ? friendship.getUser1unreadnum() : friendship.getUser2unreadnum());
            friend.put("timestamp", friendship.getEndtime() != null ? sdf.format(friendship.getEndtime()) : "");
            friend.put("messageEnd", friendship.getEndmessage());
            friend.put("isRobot", friendship.getIsRobot());

            result.add(friend);
        }

        return AjaxResult.success(result);
    }
}
