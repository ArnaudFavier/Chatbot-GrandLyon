package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.dao.UserDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nico on 24/04/2017.
 */

public class RegisterTask extends AsyncTask<String, String, HttpResult> {
    private RegisterActivity registerActivity;
    private UserDAO userDAO;
    private MessageDAO messageDAO;

    public RegisterTask(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
        this.userDAO = new UserDAO(registerActivity);
        this.messageDAO = new MessageDAO(registerActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected HttpResult doInBackground(String... params) {
        String firstName = params[0];
        String lastName = params[1];
        String email = params[2];
        String password = params[3];

        String url = registerActivity.getString(R.string.server_url) + registerActivity.getString(R.string.server_register);
        HttpResult httpResult = CallAPI.register(url,firstName,lastName,email,password);
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
                    messageDAO.close();
                    long id = userDAO.insert(user);
                    tmp = user;
                    tmp.setId(id);
                }
                DataSingleton.getInstance().setUser(tmp);

                userDAO.close();

            } catch (JSONException e) {
                httpResult.setCode(-1);
            }
        }
        return httpResult;
    }


    @Override
    protected void onPostExecute(HttpResult result) {
        registerActivity.registered(result);
    }

}