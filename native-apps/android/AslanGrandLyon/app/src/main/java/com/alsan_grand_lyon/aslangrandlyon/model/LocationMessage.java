package com.alsan_grand_lyon.aslangrandlyon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Nico on 26/04/2017.
 */

public class LocationMessage extends TextMessage {

    private double latitude = -1;
    private double longitude = -1;

    public LocationMessage(Date date, String userId, String text, double latitude, double longitude) {
        super(date,userId,text);
        this.latitude = latitude;
        this.longitude = longitude;
        setJsonBody(latitude,longitude);
    }

    public LocationMessage(String serverId, Date date, boolean isAslan, String userId, String text, double latitude, double longitude) {
        super(serverId, date, isAslan, userId, text);
        this.latitude = latitude;
        this.longitude = longitude;
        setJsonBody(latitude,longitude);
    }

    public LocationMessage(int id, String serverId, Date date, boolean isAslan, String userId, String text, double latitude, double longitude) {
        super(id, serverId, date, isAslan, userId, text);
        this.latitude = latitude;
        this.longitude = longitude;
        setJsonBody(latitude,longitude);
    }

    public LocationMessage(Date date, String jsonBody, boolean isAslan, String userId, String text, double latitude, double longitude) {
        super(date, jsonBody, isAslan, userId, text);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationMessage(String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text, double latitude, double longitude) {
        super(serverId, date, jsonBody, isAslan, userId, text);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationMessage(long id, String serverId, Date date, String jsonBody, boolean isAslan, String userId, String text, double latitude, double longitude) {
        super(id, serverId, date, jsonBody, isAslan, userId, text);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private void setJsonBody(double latitude, double longitude) {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type","location_answer");
            jsonMessage.put("text",getText());
            jsonMessage.put("lat",latitude);
            jsonMessage.put("long",longitude);
            jsonBody = jsonMessage.toString();
        } catch (JSONException je) {

        }
    }

    @Override
    public String toString() {
        return "LocationMessage{" +
                "Super=" + super.toString() +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
