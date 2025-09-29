package com.jupiter.chatweb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsUpdateDTO {
    private Integer id;
    private String merchant; // 商家用户名（用于鉴权）
    private String name;
    private BigDecimal price;
    private String src;      // 商品图片URL
    private Integer islist;  // 上架状态 1-上架 0-下架
    private String usertoken;

    // Getter and Setter
    // 可选：添加参数校验注解
    @Override
    public String toString() {
        return "GoodsUpdateDTO{" +
                "id=" + id +
                ", merchant='" + merchant + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", src='" + src + '\'' +
                ", islist=" + islist +
                '}';
    }
}