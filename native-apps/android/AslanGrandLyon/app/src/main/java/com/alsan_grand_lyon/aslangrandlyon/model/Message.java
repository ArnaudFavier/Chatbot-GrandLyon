package com.alsan_grand_lyon.aslangrandlyon.model;

import java.util.Date;

/**
 * Created by Nico on 21/04/2017.
 */

public class Message {

    private long id = -1;
    private String serverId = null;
    private String userId = null;
    private Date date = null;
    protected String jsonBody = null;
    private Boolean isAslan = false;

    public Message(Date date, String userId) {
        this.date = date;
        this.userId = userId;
    }

    public Message(String serverId, Date date, boolean isAslan, String userId) {
        this.serverId = serverId;
        this.date = date;
        this.isAslan = isAslan;
        this.userId = userId;
    }

    public Message(int id, String serverId, Date date, boolean isAslan, String userId) {
        this.id = id;
        this.serverId = serverId;
        this.date = date;
        this.isAslan = isAslan;
        this.userId = userId;
    }

    public Message(Date date, String jsonBody, boolean isAslan, String userId) {
        this.date = date;
        this.jsonBody = jsonBody;
        this.isAslan = isAslan;
        this.userId = userId;
    }

    public Message(String serverId, Date date, String jsonBody, boolean isAslan, String userId) {
        this.serverId = serverId;
        this.date = date;
        this.jsonBody = jsonBody;
        this.isAslan = isAslan;
        this.userId = userId;
    }

    public Message(int id, String serverId, Date date, String jsonBody, boolean isAslan, String userId) {
        this.id = id;
        this.serverId = serverId;
        this.date = date;
        this.jsonBody = jsonBody;
        this.isAslan = isAslan;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean isAslan() {
        return isAslan;
    }

    public void setAslan(Boolean aslan) {
        isAslan = aslan;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", serverId='" + serverId + '\'' +
                ", userId='" + userId + '\'' +
                ", date=" + date +
                ", jsonBody='" + jsonBody + '\'' +
                ", isAslan=" + isAslan +
                '}';
    }
}
