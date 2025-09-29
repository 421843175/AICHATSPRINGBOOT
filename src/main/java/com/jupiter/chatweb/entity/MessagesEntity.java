package com.jupiter.chatweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-15 13:09:17
 */
@Data
@TableName("messages")
public class MessagesEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 发送者username
	 */
	private String senderUsername;
	/**
	 * 接收者username
	 */
	private String receiverUsername;
	/**
	 * 消息内容（文本或加密后的多媒体路径）
	 */
	private String content;
	/**
	 * 发送时间
	 */
	private Date sendTime;
	/**
	 * 1=发送方记录，0=接收方记录
	 */
	private Integer isSend;
	/**
	 * 0=未删除，1=发送方删除，2=接收方删除
	 */
	private Integer deleteStatus;

	/**
	 * 0 不在线 1 在线
	 */
	private Integer is_active;





}
