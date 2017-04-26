package com.alsan_grand_lyon.aslangrandlyon.service;

import com.alsan_grand_lyon.aslangrandlyon.model.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nico on 24/04/2017.
 */

import org.json.JSONException;
import org.json.JSONObject;

public class CallAPI {

    private CallAPI() {}

    /**
     *
     * @param urlString
     * @param email
     * @param password
     * @return
     */
    public static HttpResult signIn(String urlString, String email, String password) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            return jsonPOSTRequest(urlString,jsonObject.toString());
        } catch (JSONException je) {
            return new HttpResult(-1,je.getMessage(),je);
        }
    }

    /**
     *
     * @param urlString
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @return
     */
    public static HttpResult register(String urlString, String firstName, String lastName,
                                      String email, String password) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("firstname", firstName);
            jsonObject.put("name", lastName);
            return jsonPOSTRequest(urlString,jsonObject.toString());
        } catch (JSONException je) {
            return new HttpResult(-1,je.getMessage(),je);
        }
    }

    /**
     *
     * @param urlString
     * @param userId
     * @param token
     * @param jsonMessageString
     * @return
     */
    public static HttpResult sendMessage(String urlString, String userId, String token, String jsonMessageString) {
        //TODO
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("token", token);
            JSONObject jsonMessage = new JSONObject(jsonMessageString);
            jsonObject.put("message",jsonMessage);
            return jsonPOSTRequest(urlString,jsonObject.toString());
        } catch (JSONException je) {
            return new HttpResult(-1,je.getMessage(),je);
        }
    }

    /**
     *
     * @param urlString
     * @param token
     * @param userId
     * @param messageId (-1 si on veux recuperer tous les messages)
     * @return
     */
    public static HttpResult getMessages(String urlString, String token, String userId, String messageId) {
        //TODO proteger argument (non null)
        // add to the url "/:token/:user_id/:message_id"
        urlString += "/" + token;
        urlString += "/" + userId;
        urlString += "/" + messageId;

        return jsonGETRequest(urlString);
    }

    private static HttpResult jsonPOSTRequest(String urlString, String jsonString) {
        HttpResult result = null;
        InputStream in = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(urlString);

            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            byte[] outputInBytes = jsonString.getBytes("UTF-8");
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            httpURLConnection.connect();

            in = new BufferedInputStream(httpURLConnection.getInputStream());
            httpURLConnection.getResponseCode();

            result = new HttpResult(httpURLConnection.getResponseCode(),getStringFromInputStream(in));
        } catch (Exception e) {
            try {
                int code = httpURLConnection.getResponseCode();
                result = new HttpResult(code,"");
            } catch (NullPointerException npe){
                result = new HttpResult(-1,npe.getMessage(),npe);
            } catch (IOException ioe) {
                result = new HttpResult(-1,ioe.getMessage(),ioe);
            }
        }

        return result;
    }

    private static HttpResult jsonGETRequest(String urlString) {
        HttpResult result = null;
        InputStream in = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            in = new BufferedInputStream(httpURLConnection.getInputStream());
            httpURLConnection.getResponseCode();

            result = new HttpResult(httpURLConnection.getResponseCode(),getStringFromInputStream(in));
        } catch (Exception e) {
            try {
                e.printStackTrace();
                int code = httpURLConnection.getResponseCode();
                result = new HttpResult(code,"");
            } catch (NullPointerException npe){
                result = new HttpResult(-1,npe.getMessage(),npe);
            } catch (IOException ioe) {
                result = new HttpResult(-1,ioe.getMessage(),ioe);
            }
        }

        return result;
    }


    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}