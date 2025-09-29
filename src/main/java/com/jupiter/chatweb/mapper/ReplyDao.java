package com.jupiter.chatweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jupiter.chatweb.entity.ReplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-21 11:17:34
 */


//-- 创建全文索引
//        ALTER TABLE reply
//        ADD FULLTEXT INDEX idx_question_ft (question)
//        WITH PARSER ngram
//        COMMENT '问题全文索引';
//

@Mapper
public interface ReplyDao extends BaseMapper<ReplyEntity> {
    @Select("SELECT * FROM reply WHERE receiver = #{merchant} AND MATCH(question) AGAINST (#{searchQuery} IN BOOLEAN MODE) > #{minScore} ORDER BY MATCH(question) AGAINST (#{searchQuery}) DESC LIMIT 1 ")
    ReplyEntity fulltextSearch(
            @Param("merchant") String merchant,
            @Param("searchQuery") String searchQuery,
            @Param("minScore") float minScore
    );
}
