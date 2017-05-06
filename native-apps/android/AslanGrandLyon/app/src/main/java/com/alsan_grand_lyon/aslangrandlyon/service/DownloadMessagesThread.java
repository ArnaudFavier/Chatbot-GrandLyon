package com.alsan_grand_lyon.aslangrandlyon.service;

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
 * Created by Nico on 30/04/2017.
 */

public class DownloadMessagesThread extends Thread {

    private ChatActivity chatActivity;
    private MessageDAO messageDAO = null;

    public DownloadMessagesThread(ChatActivity chatActivity) {
        super();
        this.chatActivity = chatActivity;
        this.messageDAO = new MessageDAO(chatActivity);
    }

    @Override
    public void run() {
        String url = chatActivity.getString(R.string.server_url) + chatActivity.getString(R.string.server_message);
        User user = DataSingleton.getInstance().getUser();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        while (true) {
            messageDAO.open();
            messageDAO.lock();

            List<Message> messages = new ArrayList<>();
            Message lastMessage = messageDAO.selectMostRecentFromServer();
            if (lastMessage != null) {
                HttpResult httpResult = CallAPI.getMessages(url, user.getToken(), user.getServerId(), "-1");
                if (httpResult.getCode() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(httpResult.getOutput());
                        JSONArray jsonArray = jsonObject.getJSONArray("messages");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Message message = MessageFactory.createMessageFromJson(jsonArray.getJSONObject(i));
                            if(!messageDAO.exists(message.getServerId())) {
                                messageDAO.insert(message);
                                messages.add(message);
                            }

                        }
                    } catch (JSONException e) {
                        httpResult.setCode(-1);
                    }


                    //TODO refreshMessagesTask
                    if(messages.size() > 0) {
                        chatActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageDAO.open();
                                messageDAO.lock();
                                DataSingleton.getInstance().removeAllMessages();
                                DataSingleton.getInstance().addAllMessages(messageDAO.selectMessagesOrderByDateDesc(10, 0));
                                DataSingleton.getInstance().sortMessages();
                                chatActivity.refreshMessagesListView();
                                messageDAO.unlock();
                                messageDAO.close();
                            }
                        });
                    }
                }
            }

            messageDAO.unlock();
            messageDAO.close();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
