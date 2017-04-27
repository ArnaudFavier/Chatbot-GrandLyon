package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.Profile;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;

import java.util.List;


/**
 * Created by Nico on 01/03/2017.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    private boolean canLoadMoreMessages = true;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO
        if(position == 0) {
            convertView = View.inflate(getContext(), R.layout.loading_messages_item_layout,null);
            if(!canLoadMoreMessages) {
                ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            Message message = this.getItem(position);
            if (message.isAslan()) {
                if (message instanceof TextMessage) {
                    AslanTextMessageView aslanTextMessageView = new AslanTextMessageView(getContext());
                    aslanTextMessageView.setMessage((TextMessage) message);
                    convertView = aslanTextMessageView;
                }
            } else {
                if (message instanceof TextMessage) {
                    UserTextMessageView userTextMessageView = new UserTextMessageView(getContext());
                    userTextMessageView.setMessage((TextMessage) message);
                    convertView = userTextMessageView;
                }
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Nullable
    @Override
    public Message getItem(int position) {
        return super.getItem(position-1);
    }

    public boolean isCanLoadMoreMessages() {
        return canLoadMoreMessages;
    }

    public void setCanLoadMoreMessages(boolean canLoadMoreMessages) {
        this.canLoadMoreMessages = canLoadMoreMessages;
    }
}
