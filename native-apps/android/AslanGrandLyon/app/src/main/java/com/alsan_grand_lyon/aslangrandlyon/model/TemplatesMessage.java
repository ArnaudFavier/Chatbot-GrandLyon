package com.alsan_grand_lyon.aslangrandlyon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Nico on 26/04/2017.
 */

public class TemplatesMessage extends Message {

    private String text = null;
    List<Template> templates = null;

    public TemplatesMessage(Date date, String userId, String text, List<Template> templates) {
        super(date,userId);
        this.text = text;
        this.templates = templates;
    }

    public TemplatesMessage(String serverId, Date date, boolean isAslan, String userId, String text, List<Template> templates) {
        super(serverId, date, isAslan, userId);
        this.text = text;
        this.templates = templates;
    }

    public TemplatesMessage(int id, String serverId, Date date, boolean isAslan, String userId, String text, List<Template> templates) {
        super(id, serverId, date, isAslan, userId);
        this.text = text;
        this.templates = templates;
    }

    public TemplatesMessage(Date date, String jsonBody, boolean isAslan, String userId, String text, List<Template> templates) {
        super(date, jsonBody, isAslan, userId);
        this.text = text;
        this.templates = templates;
    }

    public TemplatesMessage(String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text, List<Template> templates) {
        super(serverId, date, jsonBody, isAslan, userId);
        this.text = text;
        this.templates = templates;
    }

    public TemplatesMessage(long id, String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text, List<Template> templates) {
        super(id, serverId, date, jsonBody, isAslan, userId);
        this.text = text;
        this.templates = templates;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }


    @Override
    public String toString() {
        return "TemplatesMessage{" +
                "Super=" + super.toString() +
                "text='" + text + '\'' +
                ", templates=" + templates +
                '}';
    }
}
