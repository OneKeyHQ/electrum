package org.haobtc.onekey.bean;

import org.haobtc.onekey.exception.HardWareExceptions;

/**
 * @author liyan
 * @date 12/4/20
 */

public class PyResponse<T> {
    private T result;
    private String errors;

    public PyResponse(T result, String errors) {
        this.result = result;
        this.errors = errors;
    }
    public PyResponse() {}

    public String getErrors() {
        return errors;
    }

    public T getResult() {
        return result;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
