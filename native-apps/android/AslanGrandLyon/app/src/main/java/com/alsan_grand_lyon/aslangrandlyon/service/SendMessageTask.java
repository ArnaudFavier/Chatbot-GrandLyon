package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Nico on 26/04/2017.
 */

public class SendMessageTask extends AsyncTask<Message, String, MessageHttpResult> {
    private ChatActivity chatActivity;
    private MessageDAO messageDAO;

    public SendMessageTask(ChatActivity registerActivity) {
        this.chatActivity = registerActivity;
        this.messageDAO = new MessageDAO(registerActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected MessageHttpResult doInBackground(Message... params) {
        Message message = params[0];
        String userId = message.getUserId();
        String token = DataSingleton.getInstance().getUser().getToken();
        String jsonMessageString =  message.getJsonBody();

        String url = chatActivity.getString(R.string.server_url)
                + chatActivity.getString(R.string.server_message);
        HttpResult httpResult = CallAPI.sendMessage(url,userId,token,jsonMessageString);
        System.out.println("--->SendMessageTask.doInBackground : " + message);
        MessageHttpResult messageHttpResult = new MessageHttpResult(httpResult.getCode(),
                httpResult.getOutput(),httpResult.getException(),message);
        if(httpResult.getCode() == 200) {
            try {
                JSONObject jsonObject = new JSONObject(httpResult.getOutput());
                message.setServerId(jsonObject.getString("_id"));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                message.setDate(dateFormat.parse(jsonObject.getString("date")));
            } catch (JSONException e) {
                messageHttpResult.setCode(-1);
                e.printStackTrace();
            } catch (ParseException e) {
                messageHttpResult.setCode(-1);
                e.printStackTrace();
            }
        }


        messageDAO.open();
        messageDAO.lock();
        if(message.getId() == -1) {
            message.setId(messageDAO.insert(message));
        } else {
            messageDAO.update(message);
        }
        messageDAO.unlock();
        messageDAO.close();

        return messageHttpResult;
    }


    @Override
    protected void onPostExecute(MessageHttpResult result) {
        chatActivity.messageSent(result);
    }

}