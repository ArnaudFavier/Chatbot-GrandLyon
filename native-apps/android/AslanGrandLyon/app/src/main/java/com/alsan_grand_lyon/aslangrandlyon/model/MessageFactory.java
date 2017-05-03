package com.alsan_grand_lyon.aslangrandlyon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            boolean isAslan = jsonObject.getBoolean("isAslan");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = dateFormat.parse(jsonObject.getString("date"));

            JSONObject body = new JSONObject(jsonBody);
            String type = body.getString("type");
            if(type.equals("text")) {
                String text = body.getString("text");
                message = new TextMessage(serverId,date,jsonBody,isAslan,userId,text);
            } else if (type.equals("quickreplies")) {
                String text = body.getString("text");
                List<String> quickReplies = new ArrayList<>();
                JSONArray quickrepliesJson = body.getJSONArray("quickreplies");
                for (int i = 0; i < quickrepliesJson.length(); i++) {
                    quickReplies.add(quickrepliesJson.getString(i));
                }
                message = new QuickReplyMessage(serverId,date,jsonBody,isAslan,userId,text,quickReplies);
            } else if (type.equals("location")) {
                String text = body.getString("text");
                message = new LocationQuickReplyMessage(serverId,date,jsonBody,isAslan,userId,text);
            } else if (type.equals("location_answer")) {
                String text = body.getString("text");
                double latitude = body.getDouble("lat");
                double longitude = body.getDouble("long");
                message = new LocationMessage(serverId,date,jsonBody,isAslan,userId,text,latitude,longitude);
            } else if (type.equals("template")) {
                String text = body.getString("text");
                List<Template> templates = new ArrayList<>();
                if(!body.isNull("attachment")) {
                    JSONArray jsonAttachment = body.getJSONArray("attachment");
                    for(int i = 0; i < jsonAttachment.length(); i++) {
                        JSONObject jsonTemplate = (JSONObject)jsonAttachment.get(i);
                        String title = jsonTemplate.getString("title");
                        String imageUrl = jsonTemplate.getString("image_url");
                        String subtitle = jsonTemplate.getString("subtitle");
                        String url = jsonTemplate.getString("url");
                        String buttonUrl = jsonTemplate.getString("button_url");
                        String buttonTitle = jsonTemplate.getString("button_title");
                        Template template = new Template(title,imageUrl,subtitle,url,buttonUrl,buttonTitle);
                        templates.add(template);
                    }
                }
                message = new TemplatesMessage(serverId,date,jsonBody,isAslan,userId,text,templates);
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
            } else if (type.equals("quickreplies")) {
                String text = body.getString("text");
                List<String> quickReplies = new ArrayList<>();
                JSONArray quickrepliesJson = body.getJSONArray("quickreplies");
                for (int i = 0; i < quickrepliesJson.length(); i++) {
                    quickReplies.add(quickrepliesJson.getString(i));
                }
                message = new QuickReplyMessage(serverId,date,jsonBody,isAslan,userId,text,quickReplies);
            } else if (type.equals("location")) {
                String text = body.getString("text");
                message = new LocationQuickReplyMessage(id,serverId,date,jsonBody,isAslan,userId,text);
            } else if (type.equals("location_answer")) {
                String text = body.getString("text");
                double latitude = body.getDouble("lat");
                double longitude = body.getDouble("long");
                message = new LocationMessage(id,serverId,date,jsonBody,isAslan,userId,text,latitude,longitude);
            } else if (type.equals("template")) {
                String text = body.getString("text");
                List<Template> templates = new ArrayList<>();
                if(!body.isNull("attachment")) {
                    JSONArray jsonAttachment = body.getJSONArray("attachment");
                    for(int i = 0; i < jsonAttachment.length(); i++) {
                        JSONObject jsonTemplate = (JSONObject)jsonAttachment.get(i);
                        String title = jsonTemplate.getString("title");
                        String imageUrl = jsonTemplate.getString("image_url");
                        String subtitle = jsonTemplate.getString("subtitle");
                        String url = jsonTemplate.getString("url");
                        String buttonUrl = jsonTemplate.getString("button_url");
                        String buttonTitle = jsonTemplate.getString("button_title");
                        Template template = new Template(title,imageUrl,subtitle,url,buttonUrl,buttonTitle);
                        templates.add(template);
                    }
                }
                message = new TemplatesMessage(id,serverId,date,jsonBody,isAslan,userId,text,templates);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }
}
