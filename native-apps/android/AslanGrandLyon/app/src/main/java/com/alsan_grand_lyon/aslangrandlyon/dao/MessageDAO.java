package com.alsan_grand_lyon.aslangrandlyon.dao;

import android.content.Context;

import com.alsan_grand_lyon.aslangrandlyon.model.Message;

/**
 * Created by Nico on 24/04/2017.
 */

public class MessageDAO extends DAOBase {
    public static final String MESSAGE_KEY = "id";
    public static final String MESSAGE_DATE = "date";
    public static final String MESSAGE_JSON_BODY = "json_body";
    public static final String MESSAGE_PROFILE = "profile";
    public static final String MESSAGE_TABLE_NAME = "Message";
    public static final String MESSAGE_TABLE_CREATE = "CREATE TABLE " + MESSAGE_TABLE_NAME + " (" +
            MESSAGE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MESSAGE_DATE + " TEXT, " +
            MESSAGE_JSON_BODY + " TEXT, " +
            MESSAGE_PROFILE + " INTEGER);";
    public static final String MESSAGE_TABLE_DROP = "DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME + ";";

    public MessageDAO(Context context) {
        super(context);
    }

    public void add(Message message) {
    }

    public void delete(long id) {
    }

    public void update(Message message) {
    }

    public Message select(long id) {
        return null;
    }
}
