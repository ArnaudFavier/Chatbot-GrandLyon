package com.alsan_grand_lyon.aslangrandlyon.service;

import android.content.Context;
import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.dao.MessageDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.MessageFactory;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.service.interfaces.LoadingMessageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nico on 24/04/2017.
 */

public class GetMessagesTask extends AsyncTask<User, String, HttpResult> {
    private LoadingMessageActivity loadingMessageActivity = null;
    private Context context = null;
    private MessageDAO messageDAO = null;

    public GetMessagesTask(Context context, LoadingMessageActivity loadingMessageActivity) {
        this.context = context;
        this.loadingMessageActivity = loadingMessageActivity;
        this.messageDAO = new MessageDAO(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected HttpResult doInBackground(User... params) {
        User user = params[0];
        String url = context.getString(R.string.server_url) + context.getString(R.string.server_message);

        messageDAO.open();
        Message lastMessage = messageDAO.selectMostRecentFromServer();
        HttpResult httpResult = null;
        if(lastMessage == null) {
            httpResult = CallAPI.getMessages(url,user.getToken(),user.getServerId(),"-1");
        } else {
            httpResult = CallAPI.getMessages(url,user.getToken(),user.getServerId(),lastMessage.getServerId());
        }


        if(httpResult.getCode() == 200) {
            try {
                JSONObject jsonObject = new JSONObject(httpResult.getOutput());
                JSONArray jsonArray = jsonObject.getJSONArray("messages");
                for(int i = 0; i < jsonArray.length(); i++) {
                    Message message = MessageFactory.createMessageFromJson(jsonArray.getJSONObject(i));
                    messageDAO.insert(message);
                }
            } catch (JSONException e) {
                httpResult.setCode(-1);
            }
        }

        DataSingleton.getInstance().addAllMessages(messageDAO.selectMessagesOrderByDateDesc(10,0));
        DataSingleton.getInstance().sortMessages();

        messageDAO.close();
        return httpResult;
    }


    @Override
    protected void onPostExecute(HttpResult result) {
        loadingMessageActivity.messagesLoaded(result);
    }

}