package com.jupiter.chatweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "ai_model", autoResultMap = true)
public class AIModel {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String gender; // Male/Female

    @TableField(typeHandler = JacksonTypeHandler.class)  //指定是json类型 和MYSQL对应
    private String layers;

    @TableField(fill = FieldFill.INSERT)
    private Date updateTime;



//    @Data
//    public static class LayerStyle {
//        private String width;
//        private String height;
//        private String transform;
//        private Integer zIndex;
//    }
}