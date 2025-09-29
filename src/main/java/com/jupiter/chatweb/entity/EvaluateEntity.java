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
 * @date 2025-03-28 14:41:44
 */
@Data
@TableName("evaluate")
public class EvaluateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	/**
	 * 评分的是哪个商家
	 */
	private String merchant;
	/**
	 * 评分用户
	 */
	private String username;
	/**
	 * 评级分数
	 */
	private Float score;
	/**
	 * 评价内容
	 */
	private String content;
	
	
	//创建时间
	private Date createTime;

}
