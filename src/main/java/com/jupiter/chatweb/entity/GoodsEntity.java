package com.jupiter.chatweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
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
@TableName("goods")
public class GoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	/**
	 * 商家username
	 */
	private String merchant;
	/**
	 * 时间
	 */
	private Date date;
	/**
	 * 是否上架 1 是上架 0 否上架
	 */
	private Integer islist;
	/**
	 * 商品图片的链接
	 */
	private String src;
	/**
	 * 商品名
	 */
	private String name;
	/**
	 * 价格
	 */
	private BigDecimal price;

}
