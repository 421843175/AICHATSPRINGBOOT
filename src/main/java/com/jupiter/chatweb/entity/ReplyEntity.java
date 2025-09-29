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
 * @date 2025-03-21 11:17:34
 */
@Data
@TableName("reply")
public class ReplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	/**
	 * 接受的商铺（发送给信息的商铺）
	 */
	private String receiver;
	/**
	 * 问题
	 */
	private String question;
	/**
	 * 回答
	 */
	private String answer;
	/**
	 * 优先级 默认不填
	 */
	private Integer prority;

}
