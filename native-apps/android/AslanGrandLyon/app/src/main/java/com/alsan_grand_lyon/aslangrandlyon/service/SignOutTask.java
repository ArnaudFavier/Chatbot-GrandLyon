package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.dao.UserDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;


/**
 * Created by Nico on 24/04/2017.
 */

public class SignOutTask extends AsyncTask<String, String, Integer> {
    private ChatActivity chatActivity;
    private UserDAO userDAO;
    private MessageDAO messageDAO;

    public SignOutTask(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.userDAO = new UserDAO(chatActivity);
        this.messageDAO = new MessageDAO(chatActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Integer doInBackground(String... params) {
        try {
            userDAO.open();
            userDAO.deleteAll();
            userDAO.close();

            messageDAO.open();
            messageDAO.deleteAll();
            messageDAO.close();

            DataSingleton.getInstance().setUser(null);
            DataSingleton.getInstance().removeAllMessages();
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }


    @Override
    protected void onPostExecute(Integer result) {
        chatActivity.signedOut(result);
    }

}