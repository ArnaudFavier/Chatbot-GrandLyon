package com.alsan_grand_lyon.aslangrandlyon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Nico on 26/04/2017.
 */

public class TextMessage extends Message {

    private String text = null;

    public TextMessage(Date date, String userId, String text) {
        super(date,userId);
        this.text = text;
        setJsonBody(text);
    }

    public TextMessage(String serverId, Date date, boolean isAslan, String userId, String text) {
        super(serverId, date, isAslan, userId);
        this.text = text;
        setJsonBody(text);
    }

    public TextMessage(int id, String serverId, Date date, boolean isAslan, String userId, String text) {
        super(id, serverId, date, isAslan, userId);
        this.text = text;
        setJsonBody(text);
    }

    public TextMessage(Date date, String jsonBody, boolean isAslan, String userId, String text) {
        super(date, jsonBody, isAslan, userId);
        this.text = text;
    }

    public TextMessage(String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text) {
        super(serverId, date, jsonBody, isAslan, userId);
        this.text = text;
    }

    public TextMessage(long id, String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text) {
        super(id, serverId, date, jsonBody, isAslan, userId);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private void setJsonBody(String text) {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type","text");
            jsonMessage.put("text",text);
            jsonBody = jsonMessage.toString();
        } catch (JSONException je) {

        }
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "Super=" + super.toString() +
                "text='" + text + '\'' +
                '}';
    }
}
