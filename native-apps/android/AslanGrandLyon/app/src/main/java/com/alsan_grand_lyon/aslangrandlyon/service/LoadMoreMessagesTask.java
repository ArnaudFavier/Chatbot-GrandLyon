package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;

import java.util.List;

/**
 * Created by Nico on 24/04/2017.
 */

public class LoadMoreMessagesTask extends AsyncTask<Integer, String, Integer> {
    private ChatActivity chatActivity = null;
    private MessageDAO messageDAO = null;

    public LoadMoreMessagesTask(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.messageDAO = new MessageDAO(chatActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Integer doInBackground(Integer... params) {
        int limit = params[0];
        int offset = params[1];

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageDAO.open();
        List<Message> messages = messageDAO.selectMessagesOrderByDateDesc(limit,offset);
        DataSingleton.getInstance().addAllMessages(messages);
        DataSingleton.getInstance().sortMessages();
        messageDAO.close();

        return messages.size();
    }


    @Override
    protected void onPostExecute(Integer result) {
        chatActivity.moreMessagesLoaded(result);
    }

}