package com.alsan_grand_lyon.aslangrandlyon.model;

/**
 * Created by Nico on 21/04/2017.
 */

public class TextMessage {

    private Profile profile = null;
    private String text = null;

    public TextMessage(Profile profile, String text) {
        this.profile = profile;
        this.text = text;
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
