package com.sv.score.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDto<T> implements Serializable {


    private static final long serialVersionUID = 6864978672407596458L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private boolean success;

    private String errorMsg;

    private T data;

    @JsonIgnore
    private HttpStatus status;

    public ResponseDto() {
    }

    public ResponseDto(T data) {
        this.data = data;
        this.success = true;
        this.status = HttpStatus.OK;
    }

    public ResponseDto(String errorMsg, HttpStatus status) {
        this.success = false;
        this.errorMsg = errorMsg;
        this.status = status;
    }

    public ResponseDto(boolean success, String errorMsg, T data, HttpStatus status) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.data = data;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
