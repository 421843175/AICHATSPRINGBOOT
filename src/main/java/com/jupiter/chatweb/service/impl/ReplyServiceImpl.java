package com.jupiter.chatweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.chatweb.chat.server.service.ChannelService;
import com.jupiter.chatweb.entity.ReplyEntity;
import com.jupiter.chatweb.mapper.ReplyDao;
import com.jupiter.chatweb.service.ChatService;
import com.jupiter.chatweb.service.ReplyService;
import com.jupiter.chatweb.util.AjaxResult;
import com.jupiter.chatweb.util.KeywordExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReplyServiceImpl implements ReplyService {
    @Autowired
    private ReplyDao replyDao;

    @Autowired
    private ChatService chatService;


    @Autowired  // 关键注解  注入环境.yaml
    private Environment env;  // 注入环境变量

    @Override
    public List<ReplyEntity> getSuggestions(String merchant, Integer goodsId) {
        return replyDao.selectList(
                new QueryWrapper<ReplyEntity>()
                        .eq("receiver", merchant)
                        .orderByAsc("RAND()")  // 使用随机排序
                        .last("LIMIT 6")       // 随机取6条
        );
    }

//    寻找库中最匹配的答案
    public ReplyEntity findByQuestionAndMerchant(String question, String merchant, String user) {
        // 1. 关键词提取与格式化
        List<String> keywords = KeywordExtractor.extractKeywords(question);
        if (keywords.isEmpty()) return null;
        String searchQuery = keywords.stream()
                .map(k -> "+" + k)
                .collect(Collectors.joining(" "));

        // 2. 读取配置
        float minScore = env.getProperty("reply.min-match-score", Float.class, 0.3f);

        // 3. 执行查询并获取结果
        ReplyEntity replyEntity = replyDao.fulltextSearch(merchant, searchQuery, minScore); // 关键修复点

        // 4. 保存聊天记录
        if (replyEntity != null) {
            chatService.saveMessage(merchant, user, replyEntity.getAnswer());
        }

        return replyEntity;
    }


    @Override
    public List<ReplyEntity> getRepliesByMerchant(String merchant) {
        return replyDao.selectList(
                new QueryWrapper<ReplyEntity>()
                        .eq("receiver", merchant)
                        .orderByDesc("prority")
        );
    }

    @Override
    public AjaxResult saveReply(ReplyEntity entity) {
        try {
            // 校验必填字段
            if (entity.getQuestion() == null || entity.getQuestion().trim().isEmpty()) {
                return AjaxResult.error("问题不能为空");
            }
            if (entity.getAnswer() == null || entity.getAnswer().trim().isEmpty()) {
                return AjaxResult.error("回答不能为空");
            }

            // 设置默认优先级
            if (entity.getPrority() == null) {
                entity.setPrority(0);
            }

            int result = entity.getId() == null ?
                    replyDao.insert(entity) :
                    replyDao.updateById(entity);

            return AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存问答失败: {}", e.getMessage());
            return AjaxResult.error("服务器错误");
        }
    }

    @Override
    public AjaxResult deleteReply(Integer id, String merchant) {
        try {
            // 验证归属
            ReplyEntity exist = replyDao.selectOne(
                    new QueryWrapper<ReplyEntity>()
                            .eq("id", id)
                            .eq("receiver", merchant)
            );

            if (exist == null) {
                return AjaxResult.error("问答不存在");
            }

            int result = replyDao.deleteById(id);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("删除问答失败: {}", e.getMessage());
            return AjaxResult.error("服务器错误");
        }
    }

}
