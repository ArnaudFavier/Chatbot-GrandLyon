package com.alsan_grand_lyon.aslangrandlyon.service;


import com.alsan_grand_lyon.aslangrandlyon.model.User;

/**
 * Created by Nico on 24/04/2017.
 */

public class PostResult {
    private int code = 0;
    private String output = null;
    private Exception exception = null;

    public PostResult(int code, String output) {
        this.code = code;
        this.output = output;
        this.exception = null;
    }

    public PostResult(int code, String output, Exception exception) {
        this.code = code;
        this.output = output;
        this.exception = exception;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
