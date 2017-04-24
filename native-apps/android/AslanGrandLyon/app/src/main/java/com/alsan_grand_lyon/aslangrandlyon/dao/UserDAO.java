package com.alsan_grand_lyon.aslangrandlyon.dao;

import android.content.Context;

import com.alsan_grand_lyon.aslangrandlyon.model.User;

/**
 * Created by Nico on 24/04/2017.
 */

public class UserDAO extends DAOBase {
    public static final String USER_KEY = "id";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_EMAIL_NAME = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_TABLE_NAME = "User";
    public static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE_NAME + " (" +
            USER_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_FIRST_NAME + " TEXT, " +
            USER_LAST_NAME + " TEXT, " +
            USER_EMAIL_NAME + " TEXT, " +
            USER_PASSWORD + " TEXT);";
    public static final String USER_TABLE_DROP = "DROP TABLE IF EXISTS " + USER_TABLE_NAME + ";";

    public UserDAO(Context context) {
        super(context);
    }

    public void add(User user) {
    }

    public void delete(long id) {
    }

    public void update(User user) {
    }

    public User select(long id) {
        return null;
    }
}
