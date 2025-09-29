package com.jupiter.chatweb.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("统一响应工具类")
//继承序列化
public class AjaxResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("返回码")
    private int code;

    @ApiModelProperty("返回信息")
    private String message;

    @ApiModelProperty("返回数据")
    private T data;

    public AjaxResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public AjaxResult(T data, int code){
        this.data=data;
        this.code=code;
    }


    //成功调用
    public static<T> AjaxResult<T> success(T data){
       return new AjaxResult<T>(data,200);
    }

    //失败调用
    public static AjaxResult error( String message){
        return new AjaxResult(403,message);
    }

}
