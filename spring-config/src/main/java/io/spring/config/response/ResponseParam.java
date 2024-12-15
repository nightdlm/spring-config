package io.spring.config.response;

import java.util.List;
import java.util.Map;

public class ResponseParam {
    private Integer code;
    private String message;
    private Map<String,String> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String,String> getData() {
        return data;
    }

    public void setData(Map<String,String> data) {
        this.data = data;
    }
}
