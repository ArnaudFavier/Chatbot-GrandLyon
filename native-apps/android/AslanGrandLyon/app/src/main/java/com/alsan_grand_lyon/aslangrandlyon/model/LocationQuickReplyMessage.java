package com.alsan_grand_lyon.aslangrandlyon.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Nico on 26/04/2017.
 */

public class LocationQuickReplyMessage extends TextMessage {

    public LocationQuickReplyMessage(Date date, String jsonBody, boolean isAslan, String userId, String text) {
        super(date, jsonBody, isAslan, userId,text);
    }

    public LocationQuickReplyMessage(String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text) {
        super(serverId, date, jsonBody, isAslan, userId,text);
    }

    public LocationQuickReplyMessage(long id, String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text) {
        super(id, serverId, date, jsonBody, isAslan, userId,text);
    }


    @Override
    public String toString() {
        return "QuickReplyMessage{" +
                "Super=" + super.toString() +
                '}';
    }
}
