package com.alsan_grand_lyon.aslangrandlyon.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.MessageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 24/04/2017.
 */

public class MessageDAO extends AbstractDAO {
    public static final String MESSAGE_KEY = "id";
    public static final String MESSAGE_SERVER_ID = "server_id";
    public static final String MESSAGE_USER_ID = "user_id";
    public static final String MESSAGE_JSON_BODY = "json_body";
    public static final String MESSAGE_DATE_LONG = "date_long";
    public static final String MESSAGE_IS_ASLAN = "is_aslan";
    public static final String MESSAGE_TABLE_NAME = "Message";
    public static final String MESSAGE_TABLE_CREATE = "CREATE TABLE " + MessageDAO.MESSAGE_TABLE_NAME + " (" +
            MessageDAO.MESSAGE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MessageDAO.MESSAGE_SERVER_ID + " TEXT, " +
            MessageDAO.MESSAGE_USER_ID + " TEXT, " +
            MessageDAO.MESSAGE_JSON_BODY + " TEXT, " +
            MessageDAO.MESSAGE_DATE_LONG + " INTEGER, " +
            MessageDAO.MESSAGE_IS_ASLAN + " BOOLEAN NOT NULL CHECK (" + MessageDAO.MESSAGE_IS_ASLAN + " IN (0,1)));";
    public static final String MESSAGE_TABLE_DROP = "DROP TABLE IF EXISTS " + MessageDAO.MESSAGE_TABLE_NAME + ";";

    public MessageDAO(Context context) {
        super(context);
    }

    public long insert(Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageDAO.MESSAGE_SERVER_ID, message.getServerId());
        values.put(MessageDAO.MESSAGE_USER_ID, message.getUserId());
        values.put(MessageDAO.MESSAGE_JSON_BODY, message.getJsonBody());
        if(message.getDate() != null) {
            values.put(MessageDAO.MESSAGE_DATE_LONG, message.getDate().getTime());
        } else {
            values.put(MessageDAO.MESSAGE_DATE_LONG, -1);
        }
        values.put(MessageDAO.MESSAGE_IS_ASLAN, message.isAslan());
        return sqLiteDatabase.insert(MessageDAO.MESSAGE_TABLE_NAME, null, values);
    }

    public void delete(long id) {
        String whereClause = MessageDAO.MESSAGE_KEY + " = ?";
        String[] whereArgs = new String[] { String.valueOf(MessageDAO.MESSAGE_KEY) };
        sqLiteDatabase.delete(MessageDAO.MESSAGE_TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteAll() {
        sqLiteDatabase.delete(MessageDAO.MESSAGE_TABLE_NAME, null, null);
    }

    public void update(Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageDAO.MESSAGE_SERVER_ID, message.getServerId());
        values.put(MessageDAO.MESSAGE_USER_ID, message.getUserId());
        values.put(MessageDAO.MESSAGE_JSON_BODY, message.getJsonBody());
        if(message.getDate() != null) {
            values.put(MessageDAO.MESSAGE_DATE_LONG, message.getDate().getTime());
        } else {
            values.put(MessageDAO.MESSAGE_DATE_LONG, -1);
        }
        values.put(MessageDAO.MESSAGE_IS_ASLAN, message.isAslan());
        String whereClause = MessageDAO.MESSAGE_KEY + " = ?";
        String[] whereArgs = new String[] { String.valueOf(MessageDAO.MESSAGE_KEY) };
        sqLiteDatabase.update(MessageDAO.MESSAGE_TABLE_NAME,values,whereClause,whereArgs);
    }

    public Message select(long id) {
        return null;
    }

    public Message selectMostRecentFromServer() {
        Message message = null;
        String[] columns = {MessageDAO.MESSAGE_KEY, MessageDAO.MESSAGE_SERVER_ID, MessageDAO.MESSAGE_USER_ID,
                MessageDAO.MESSAGE_JSON_BODY, MessageDAO.MESSAGE_DATE_LONG, MessageDAO.MESSAGE_IS_ASLAN};
        String selection = MessageDAO.MESSAGE_SERVER_ID + " IS NOT NULL";
        String orderBy = MessageDAO.MESSAGE_DATE_LONG + " DESC";
        String limit = "1";
        Cursor cursor = sqLiteDatabase.query(MessageDAO.MESSAGE_TABLE_NAME, columns, selection, null, null, null, orderBy,limit);

        int count = cursor.getCount();

        if(count != 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(0);
            String serverId = cursor.getString(1);
            String userId = cursor.getString(2);
            String jsonBody = cursor.getString(3);
            long date = cursor.getLong(4);
            boolean isAslan = true;
            if(cursor.getInt(5) == 0) {
                isAslan = false;
            }
            message = MessageFactory.createMessage(id,serverId,userId,jsonBody,date,isAslan);
        }

        cursor.close();
        return message;
    }

    public List<Message> selectMessagesOrderByDateDesc(int limit, int offset) {
        String[] columns = {MessageDAO.MESSAGE_KEY, MessageDAO.MESSAGE_SERVER_ID, MessageDAO.MESSAGE_USER_ID,
                MessageDAO.MESSAGE_JSON_BODY, MessageDAO.MESSAGE_DATE_LONG, MessageDAO.MESSAGE_IS_ASLAN};
        String selection = MessageDAO.MESSAGE_SERVER_ID + " IS NOT NULL";
        String orderBy = MessageDAO.MESSAGE_DATE_LONG + " DESC";
        String limitOffset = offset + "," + limit;
        Cursor cursor = sqLiteDatabase.query(MessageDAO.MESSAGE_TABLE_NAME, columns, selection, null, null, null, orderBy,limitOffset);

        List<Message> messages = new ArrayList<>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String serverId = cursor.getString(1);
            String userId = cursor.getString(2);
            String jsonBody = cursor.getString(3);
            long date = cursor.getLong(4);
            boolean isAslan = true;
            if(cursor.getInt(5) == 0) {
                isAslan = false;
            }
            Message message = MessageFactory.createMessage(id,serverId,userId,jsonBody,date,isAslan);
            messages.add(message);
        }
        cursor.close();
        return messages;
    }
}
