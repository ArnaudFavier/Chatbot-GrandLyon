package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alsan_grand_lyon.aslangrandlyon.model.Profile;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;

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

        Message message = this.getItem(position);

        if(message.getProfile() == Profile.ASLAN) {
            convertView = new AslanTextMessageView(getContext());
        } else {
            convertView = new UserTextMessageView(getContext());
        }

        if(convertView instanceof AslanTextMessageView) {
            AslanTextMessageView textMessageView = (AslanTextMessageView) convertView;
            textMessageView.setMessage(message);
        } else {
            UserTextMessageView textMessageView = (UserTextMessageView) convertView;
            textMessageView.setMessage(message);
        }


        return convertView;
    }


}
