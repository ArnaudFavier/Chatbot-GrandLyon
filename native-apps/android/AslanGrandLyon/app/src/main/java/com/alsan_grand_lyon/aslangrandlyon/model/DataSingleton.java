package com.alsan_grand_lyon.aslangrandlyon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nico on 25/04/2017.
 */

public class DataSingleton {
    private User user = null;
    private List<Message> messages = null;
    private static DataSingleton instance = null;


    private DataSingleton() {
        messages = new ArrayList<>();
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

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    public void removeAllMessages() {
        messages.clear();
    }

    public void sortMessages() {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                if(lhs.getDate().getTime() - rhs.getDate().getTime() > 0) {
                    return 1;
                } else if (lhs.getDate().getTime() - rhs.getDate().getTime() == 0) {
                    return 0;
                }
                return -1;
            }
        });
    }
}
