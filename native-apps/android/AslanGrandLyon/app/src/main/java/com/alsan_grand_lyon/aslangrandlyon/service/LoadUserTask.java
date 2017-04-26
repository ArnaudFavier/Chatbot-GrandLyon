package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.dao.UserDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.view.launch.SplashActivity;


/**
 * Created by Nico on 24/04/2017.
 */

public class LoadUserTask extends AsyncTask<String, String, User> {
    private SplashActivity splashActivity;
    private UserDAO userDAO;
    private MessageDAO messageDAO;

    public LoadUserTask(SplashActivity splashActivity) {
        this.splashActivity = splashActivity;
        this.userDAO = new UserDAO(splashActivity);
        this.messageDAO = new MessageDAO(splashActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected User doInBackground(String... params) {
        userDAO.open();
        User tmp = userDAO.select();
        userDAO.close();

        DataSingleton.getInstance().setUser(tmp);

        return tmp;
    }


    @Override
    protected void onPostExecute(User result) {
        splashActivity.userLoaded(result);
    }

}