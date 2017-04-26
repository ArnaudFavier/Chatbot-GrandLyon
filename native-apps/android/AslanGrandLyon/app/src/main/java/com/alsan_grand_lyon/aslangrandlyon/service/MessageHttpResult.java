package com.alsan_grand_lyon.aslangrandlyon.service;

import com.alsan_grand_lyon.aslangrandlyon.model.Message;

/**
 * Created by Nico on 26/04/2017.
 */

public class MessageHttpResult extends HttpResult {

    public Message message = null;

    public MessageHttpResult(int code, String output, Exception exception, Message message) {
        super(code, output, exception);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
