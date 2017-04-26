package com.alsan_grand_lyon.aslangrandlyon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nico on 26/04/2017.
 */

public class MessageFactory {
    public static Message createMessageFromJson(JSONObject jsonObject) {
        Message message = null;
        try {
            String serverId = jsonObject.getString("_id");
            String userId = jsonObject.getString("user_id");
            String jsonBody = jsonObject.getString("message");
            boolean isAslan = !jsonObject.isNull("isAslan");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = dateFormat.parse(jsonObject.getString("date"));

            JSONObject body = new JSONObject(jsonBody);
            String type = body.getString("type");

            if(type.equals("text")) {
                String text = body.getString("text");
                message = new TextMessage(serverId,date,jsonBody,isAslan,userId,text);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return message;
    }

    public static Message createMessage(long id, String serverId, String userId, String jsonBody, long dateLong, boolean isAslan) {
        Message message = null;
        try {
            Date date = new Date(dateLong);

            JSONObject body = new JSONObject(jsonBody);
            String type = body.getString("type");

            if(type.equals("text")) {
                String text = body.getString("text");
                message = new TextMessage(id,serverId,date,jsonBody,isAslan,userId,text);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }
}
