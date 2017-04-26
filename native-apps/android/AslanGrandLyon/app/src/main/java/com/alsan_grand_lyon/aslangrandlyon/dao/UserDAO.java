package com.alsan_grand_lyon.aslangrandlyon.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.alsan_grand_lyon.aslangrandlyon.model.User;

/**
 * Created by Nico on 24/04/2017.
 */

public class UserDAO extends AbstractDAO {
    public static final String USER_KEY = "id";
    public static final String USER_SERVER_ID = "server_id";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_TOKEN = "token";
    public static final String USER_TABLE_NAME = "User";
    public static final String USER_TABLE_CREATE = "CREATE TABLE " + UserDAO.USER_TABLE_NAME + " (" +
            UserDAO.USER_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            UserDAO.USER_SERVER_ID + " TEXT, " +
            UserDAO.USER_FIRST_NAME + " TEXT, " +
            UserDAO.USER_LAST_NAME + " TEXT, " +
            UserDAO.USER_EMAIL + " TEXT, " +
            UserDAO.USER_PASSWORD + " TEXT, " +
            UserDAO.USER_TOKEN + " TEXT);";
    public static final String USER_TABLE_DROP = "DROP TABLE IF EXISTS " + UserDAO.USER_TABLE_NAME + ";";

    public UserDAO(Context context) {
        super(context);
    }

    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put(UserDAO.USER_SERVER_ID, user.getServerId());
        values.put(UserDAO.USER_FIRST_NAME, user.getFirstName());
        values.put(UserDAO.USER_LAST_NAME, user.getLastName());
        values.put(UserDAO.USER_EMAIL, user.getEmail());
        values.put(UserDAO.USER_PASSWORD, user.getPassword());
        values.put(UserDAO.USER_TOKEN, user.getToken());
        return sqLiteDatabase.insert(UserDAO.USER_TABLE_NAME, null, values);
    }

    public void deleteAll() {
        sqLiteDatabase.delete(UserDAO.USER_TABLE_NAME, null, null);
    }

    public void delete(long id) {
    }

    public void update(User user) {
    }

    public int count() {
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from " + UserDAO.USER_TABLE_NAME , null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public boolean existEmail(String email) {
        String selection = UserDAO.USER_EMAIL + " like ?";
        String[] selectionArgs = {email};
        Cursor cursor = sqLiteDatabase.query(UserDAO.USER_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count == 1;
    }

    public User select(String serverID) {
        User user = null;
        String selection = UserDAO.USER_SERVER_ID + " like ?";
        String[] columns = {UserDAO.USER_KEY, UserDAO.USER_FIRST_NAME, UserDAO.USER_LAST_NAME,
                UserDAO.USER_EMAIL, UserDAO.USER_PASSWORD, UserDAO.USER_TOKEN};
        String[] selectionArgs = {serverID};
        Cursor cursor = sqLiteDatabase.query(UserDAO.USER_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();

        if(count != 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String email = cursor.getString(3);
            String password = cursor.getString(4);
            String token = cursor.getString(5);
            user = new User(id,serverID,firstName,lastName,email,password,token);
        }

        cursor.close();
        return user;
    }

    public User select() {
        User user = null;
        String[] columns = {UserDAO.USER_KEY, UserDAO.USER_SERVER_ID, UserDAO.USER_FIRST_NAME,
                UserDAO.USER_LAST_NAME, UserDAO.USER_EMAIL, UserDAO.USER_PASSWORD, UserDAO.USER_TOKEN};
        Cursor cursor = sqLiteDatabase.query(UserDAO.USER_TABLE_NAME, null, null, null, null, null, null);
        int count = cursor.getCount();

        if(count != 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(0);
            String serverId = cursor.getString(1);
            String firstName = cursor.getString(2);
            String lastName = cursor.getString(3);
            String email = cursor.getString(4);
            String password = cursor.getString(5);
            String token = cursor.getString(6);
            user = new User(id,serverId,firstName,lastName,email,password,token);
        }

        cursor.close();
        return user;
    }

}
