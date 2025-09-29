package com.jupiter.chatweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-03-15 14:25:02
 */
@Data
@TableName("friendships")
public class FriendshipsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 用户ID1
	 */
	private String username1;
	/**
	 * 用户ID2
	 */
	private String username2;
	/**
	 * 
	 */
	private String username1head;
	/**
	 * 
	 */
	private String username2head;
	/**
	 * 
	 */
	private String createAt;
	/**
	 * 这是用户1给2或者2给1发的最后一条消息 我们一开始都放到redis里面 当断开链接的时候写入数据库
	 */
	private String endmessage;
	/**
	 * 这是用户1给2或者2给1发的最后一条消息时间 我们一开始都放到redis里面 当断开链接的时候写入数据库
	 */
	private Date endtime;

	private Integer user1unreadnum;

	private Integer user2unreadnum;

	//0不是机器人 是 真人客服 1 是机器人客服 默认都是机器人 然后如果转人工就变成真人客服
	private Integer isRobot;

	//用户1的角色 0管理员(客服) 1卖家 2买家
	private Integer isRole1;

	//用户2的角色 0管理员(客服) 1卖家 2买家
	private Integer isRole2;

	private Integer goodsId;

}
