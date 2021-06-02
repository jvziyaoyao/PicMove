package com.jvziyaoyao.picmove.domain.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ApiResult<T> implements Serializable {
    private int statusCode;
    private String message;
    private Date datetime;
    private T data;

    public boolean isSuccess(){
        return statusCode==200 && data!=null;
    }

}
