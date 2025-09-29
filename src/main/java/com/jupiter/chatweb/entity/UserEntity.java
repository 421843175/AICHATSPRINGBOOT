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
 * @date 2025-03-07 16:35:42
 */
@Data
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 
	 */
	private String username;
	/**
	 * 
	 */
	private String password;
	/**
	 * 
	 */
	private String nick;
	/**
	 * 
	 */
	private String head;
	private Date logintime;
	/**
	 * 角色   0卖家(即客服) 1买家  2买家
	 */
	private Integer role;

   private String perms;


	//用户通道 使用redis了
//	private String channel;

	//是否在线 使用redis了
//	private Integer isActive;


}
