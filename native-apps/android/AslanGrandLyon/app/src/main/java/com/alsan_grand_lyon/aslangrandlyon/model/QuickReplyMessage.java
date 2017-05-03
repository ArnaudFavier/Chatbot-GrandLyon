package com.alsan_grand_lyon.aslangrandlyon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nico on 26/04/2017.
 */

public class QuickReplyMessage extends TextMessage {

    private List<String> quickReplies = null;

    public QuickReplyMessage(Date date, String jsonBody, boolean isAslan, String userId, String text, List<String> quickReplies) {
        super(date, jsonBody, isAslan, userId,text);
        this.quickReplies = quickReplies;
    }

    public QuickReplyMessage(String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text, List<String> quickReplies) {
        super(serverId, date, jsonBody, isAslan, userId,text);
        this.quickReplies = quickReplies;
    }

    public QuickReplyMessage(long id, String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text, List<String> quickReplies) {
        super(id, serverId, date, jsonBody, isAslan, userId,text);
        this.quickReplies = quickReplies;
    }

    public List<String> getQuickReplies() {
        return quickReplies;
    }

    public void setQuickReplies(List<String> quickReplies) {
        this.quickReplies = quickReplies;
    }

    @Override
    public String toString() {
        return "QuickReplyMessage{" +
                "Super=" + super.toString() +
                ", quickReplies=" + quickReplies +
                '}';
    }
}
