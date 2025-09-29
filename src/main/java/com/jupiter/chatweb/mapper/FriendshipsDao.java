package com.jupiter.chatweb.mapper;

import com.jupiter.chatweb.entity.FriendshipsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-15 13:09:17
 */
@Mapper
public interface FriendshipsDao extends BaseMapper<FriendshipsEntity> {
    @Update("<script>" +
            "UPDATE friendships " +
            "SET user1unreadnum = CASE " +
            "    WHEN username1 = #{receiver,jdbcType=VARCHAR} THEN user1unreadnum + 1 " +
            "    ELSE user1unreadnum " +
            "END, " +
            "user2unreadnum = CASE " +
            "    WHEN username2 = #{receiver,jdbcType=VARCHAR} THEN user2unreadnum + 1 " +
            "    ELSE user2unreadnum " +
            "END " +
            "WHERE (username1 = #{sender,jdbcType=VARCHAR} AND username2 = #{receiver,jdbcType=VARCHAR}) " +
            "   OR (username1 = #{receiver,jdbcType=VARCHAR} AND username2 = #{sender,jdbcType=VARCHAR})" +
            "</script>")
    int updateUnreadCount(@Param("sender") String sender,
                          @Param("receiver") String receiver);

    @Update("<script>" +
            "UPDATE friendships " +
            "SET " +
            "  endmessage = #{message}, " +
            "  endtime = NOW(), " +
            "  user1unreadnum = CASE " +
            "    WHEN username1 = #{receiver} THEN user1unreadnum + 1 " +
            "    ELSE user1unreadnum " +
            "  END, " +
            "  user2unreadnum = CASE " +
            "    WHEN username2 = #{receiver} THEN user2unreadnum + 1 " +
            "    ELSE user2unreadnum " +
            "  END " +
            "WHERE (username1 = #{sender} AND username2 = #{receiver}) " +
            "   OR (username1 = #{receiver} AND username2 = #{sender})" +
            "</script>")
    int updateLastMessage(@Param("sender") String sender,
                          @Param("receiver") String receiver,
                          @Param("message") String message);


    @Update("UPDATE friendships " +
            "SET user1unreadnum = CASE " +
            "  WHEN username1 = #{receiver} AND username2 = #{sender} THEN user1unreadnum + 1 " +
            "  ELSE user1unreadnum END, " +
            "user2unreadnum = CASE " +
            "  WHEN username2 = #{receiver} AND username1 = #{sender} THEN user2unreadnum + 1 " +
            "  ELSE user2unreadnum END " +
            "WHERE (username1 = #{sender} AND username2 = #{receiver}) " +
            "   OR (username1 = #{receiver} AND username2 = #{sender})")
    void incrementUnreadCount(@Param("sender") String sender, @Param("receiver") String receiver);

}
