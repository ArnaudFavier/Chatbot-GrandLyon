package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.dao.UserDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nico on 24/04/2017.
 */

public class SignInTask extends AsyncTask<String, String, HttpResult> {
    private SignInActivity signInActivity;
    private UserDAO userDAO;
    private MessageDAO messageDAO;

    public SignInTask(SignInActivity signInActivity) {
        this.signInActivity = signInActivity;
        this.userDAO = new UserDAO(signInActivity);
        this.messageDAO = new MessageDAO(signInActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected HttpResult doInBackground(String... params) {
        String email = params[0];
        String password = params[1];
        String url = signInActivity.getString(R.string.server_url) + signInActivity.getString(R.string.server_sign_in);
        HttpResult httpResult = CallAPI.signIn(url,email,password);
        if(httpResult.getCode() == 200) {
            try {
                JSONObject jsonObject = new JSONObject(httpResult.getOutput());
                User user = new User(jsonObject.getString("_id"),
                        jsonObject.getString("firstname"),
                        jsonObject.getString("name"),
                        jsonObject.getString("email"),
                        password,
                        jsonObject.getString("token")
                );
                userDAO.open();
                User tmp = userDAO.select(user.getServerId());
                if(tmp == null) {
                    userDAO.deleteAll();
                    messageDAO.open();
                    messageDAO.deleteAll();

                    long id = userDAO.insert(user);
                    tmp = user;
                    tmp.setId(id);
                    //TODO DL messages
                    messageDAO.close();
                }
                DataSingleton.getInstance().setUser(tmp);
                //TODO setMessages

                userDAO.close();
            } catch (JSONException e) {
                httpResult.setCode(-1);
            }
        }
        return httpResult;
    }


    @Override
    protected void onPostExecute(HttpResult result) {
        signInActivity.signedIn(result);
    }

}