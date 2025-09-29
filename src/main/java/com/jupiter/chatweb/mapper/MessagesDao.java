package com.jupiter.chatweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jupiter.chatweb.entity.MessagesEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-15 13:09:17
 */
@Mapper
public interface MessagesDao extends BaseMapper<MessagesEntity> {

    void saveMessage(String sender, String receiver, String content);
	
}
