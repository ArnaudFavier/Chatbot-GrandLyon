package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alsan_grand_lyon.aslangrandlyon.model.Profile;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;

import java.util.List;


/**
 * Created by Nico on 01/03/2017.
 */

public class MessageAdapter extends ArrayAdapter<TextMessage> {

    public MessageAdapter(Context context, List<TextMessage> textMessages) {
        super(context, 0, textMessages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextMessage textMessage = this.getItem(position);

        if(textMessage.getProfile() == Profile.ASLAN) {
            convertView = new AslanTextMessageView(getContext());
        } else {
            convertView = new UserTextMessageView(getContext());
        }

        if(convertView instanceof AslanTextMessageView) {
            AslanTextMessageView textMessageView = (AslanTextMessageView) convertView;
            textMessageView.setTextMessage(textMessage);
        } else {
            UserTextMessageView textMessageView = (UserTextMessageView) convertView;
            textMessageView.setTextMessage(textMessage);
        }


        return convertView;
    }


}
