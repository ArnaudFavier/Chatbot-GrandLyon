package com.alsan_grand_lyon.aslangrandlyon.service;

import android.content.Context;
import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.MessageFactory;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 24/04/2017.
 */

public class DownloadMessagesTask extends AsyncTask<User, String, List<Message>> {
    private ChatActivity chatActivity = null;
    private MessageDAO messageDAO = null;

    public DownloadMessagesTask(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.messageDAO = new MessageDAO(chatActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected List<Message> doInBackground(User... params) {
        User user = params[0];
        String url = chatActivity.getString(R.string.server_url) + chatActivity.getString(R.string.server_message);

        int timeToSleep = 3000;
        int numberOfNewMessages = 0;
        int maxAttempt = 0;
        List<Message> messages = new ArrayList<>();

        while(numberOfNewMessages == 0 && maxAttempt < 3) {

            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            messageDAO.open();
            messageDAO.lock();

            Message lastMessage = messageDAO.selectMostRecentFromServer();
            if (lastMessage != null) {
                HttpResult httpResult = CallAPI.getMessages(url, user.getToken(), user.getServerId(), lastMessage.getServerId());
                if (httpResult.getCode() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(httpResult.getOutput());
                        JSONArray jsonArray = jsonObject.getJSONArray("messages");
                        System.out.println("--->" + jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Message message = MessageFactory.createMessageFromJson(jsonArray.getJSONObject(i));
                            if (!messageDAO.exists(message.getServerId())) {
                                messageDAO.insert(message);
                                messages.add(message);
                            }
                        }
                    } catch (JSONException e) {
                        httpResult.setCode(-1);
                    }
                } else {
                    System.out.println("--->" + httpResult.toString());
                }
            }

            messageDAO.unlock();
            messageDAO.close();

            timeToSleep = 1000;
            maxAttempt++;
            numberOfNewMessages = messages.size();
        }

        return messages;
    }


    @Override
    protected void onPostExecute(List<Message> result) {
        messageDAO.open();
        messageDAO.lock();
        DataSingleton.getInstance().removeAllMessages();
        DataSingleton.getInstance().addAllMessages(messageDAO.selectAll());
        DataSingleton.getInstance().sortMessages();
        messageDAO.unlock();
        messageDAO.close();
        chatActivity.setAslanIsTyping(false);
        chatActivity.refreshMessagesListView();
        if(result.size() > 0) {
            chatActivity.readMessages(result);
            chatActivity.scrollToBottom();
        } else {
            chatActivity.showToast(chatActivity.getString(R.string.alsan_cant_answer));
        }

    }

}