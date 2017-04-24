package com.alsan_grand_lyon.aslangrandlyon.service;

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

    public static PostResult signIn(String urlString, String email, String password) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            return jsonPOSTRequest(urlString,jsonObject.toString());
        } catch (JSONException je) {
            return new PostResult(-1,je.getMessage(),je);
        }
    }

    public static PostResult register(String urlString, String firstName, String lastName,
                                      String email, String password) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("firstname", firstName);
            jsonObject.put("name", lastName);
            return jsonPOSTRequest(urlString,jsonObject.toString());
        } catch (JSONException je) {
            return new PostResult(-1,je.getMessage(),je);
        }
    }

    private static PostResult jsonPOSTRequest(String urlString, String jsonString) {
        PostResult result = null;
        InputStream in = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(urlString);

            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
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

            result = new PostResult(httpURLConnection.getResponseCode(),getStringFromInputStream(in));
        } catch (Exception e) {
            try {
                int code = httpURLConnection.getResponseCode();
                result = new PostResult(code,getStringFromInputStream(in));
            } catch (NullPointerException npe){
                result = new PostResult(-1,npe.getMessage(),npe);
            } catch (IOException ioe) {
                result = new PostResult(-1,ioe.getMessage(),ioe);
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