package com.alsan_grand_lyon.aslangrandlyon.model;

import java.util.Date;

/**
 * Created by Nico on 21/04/2017.
 */

public class Message {

    private int id = -1;
    private Date date = null;
    private Profile profile = null;
    private String text = null;//TODO propiété calculé
    private String jsonBody = null;

    public Message(Profile profile, String text) {
        this.profile = profile;
        this.text = text;
    }

    public Message(int id, Date date, Profile profile, String jsonBody) {
        this.id = id;
        this.date = date;
        this.profile = profile;
        this.jsonBody = jsonBody;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
