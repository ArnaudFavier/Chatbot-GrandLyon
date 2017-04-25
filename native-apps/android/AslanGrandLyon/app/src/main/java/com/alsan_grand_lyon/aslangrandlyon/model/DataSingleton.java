package com.alsan_grand_lyon.aslangrandlyon.model;

import java.util.List;

/**
 * Created by Nico on 25/04/2017.
 */

public class DataSingleton {
    private User user = null;
    private List<Message> messages = null;
    private static DataSingleton instance = null;


    private DataSingleton() {
    }

    public static DataSingleton getInstance() {
        if(instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
