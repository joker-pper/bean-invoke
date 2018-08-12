package com.devloper.joker.beaninvoke.controller;

public class ResponseResult {

    private Boolean status;
    private String msg;
    private Object data;

    public ResponseResult() {
        status = true;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
