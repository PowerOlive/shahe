package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class Rsp<T> {
    private int errorCode;
    private String errorInfo;
    private T data;

    public int getErrerCode() {
        return errorCode;
    }

    public void setErrerCode(int errerCode) {
        this.errorCode = errerCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonRsp{" +
                "errerCode=" + errorCode +
                ", errorInfo='" + errorInfo + '\'' +
                ", data=" + data +
                '}';
    }
}
