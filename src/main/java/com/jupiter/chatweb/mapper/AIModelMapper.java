package com.jupiter.chatweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jupiter.chatweb.entity.AIModel;
import com.jupiter.chatweb.entity.FriendshipsEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AIModelMapper extends BaseMapper<AIModel> {
}
