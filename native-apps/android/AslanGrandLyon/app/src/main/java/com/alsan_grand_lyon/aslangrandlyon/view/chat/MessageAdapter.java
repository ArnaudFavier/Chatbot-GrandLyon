package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alsan_grand_lyon.aslangrandlyon.model.Profile;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;

import java.util.List;


/**
 * Created by Nico on 01/03/2017.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO
        Message message = this.getItem(position);

        if(message.isAslan()) {
            if(message instanceof TextMessage) {
                AslanTextMessageView aslanTextMessageView = new AslanTextMessageView(getContext());
                aslanTextMessageView.setMessage((TextMessage)message);
                convertView = aslanTextMessageView;
            }
        } else {
            if(message instanceof TextMessage) {
                UserTextMessageView userTextMessageView = new UserTextMessageView(getContext());
                userTextMessageView.setMessage((TextMessage)message);
                convertView = userTextMessageView;
            }
        }

        return convertView;
    }


}
