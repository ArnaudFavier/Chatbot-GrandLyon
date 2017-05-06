package com.alsan_grand_lyon.aslangrandlyon.model;

/**
 * Created by Nico on 21/04/2017.
 */

public enum Profile {
    //do not change int value
    ASLAN(0,"Aslan"),
    USER(1,"User");

    int value;
    String profile;
    private Profile(int value, String profile) {
        this.value = value;
        this.profile = profile;
    }

    @Override
    public String toString() {
        return profile;
    }

    public int getValue() {
        return value;
    }
}
