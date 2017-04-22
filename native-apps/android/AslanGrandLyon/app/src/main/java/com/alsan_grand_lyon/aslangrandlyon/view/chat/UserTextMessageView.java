package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;

/**
 * Created by Nico on 21/04/2017.
 */

public class UserTextMessageView extends FrameLayout {

    private LinearLayout linearLayout = null;
    private TextMessage textMessage = null;
    private TextView textView = null;
    private TextView dateTextView = null;
    private ImageView imageView = null;
    private boolean isExpended = false;
    private int linearLayoutHeight = -1;

    public UserTextMessageView(@NonNull Context context) {
        super(context);

        inflate(getContext(), R.layout.user_text_message_layout,this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        textView = (TextView) findViewById(R.id.textView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        isExpended = false;
        dateTextView.setHeight(0);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayoutHeight == -1) {
                    linearLayoutHeight = linearLayout.getHeight();
                }
                if(isExpended) {
                    ResizeAnimation resizeAnimation = new ResizeAnimation(linearLayout, linearLayoutHeight);
                    resizeAnimation.setDuration(300);
                    linearLayout.startAnimation(resizeAnimation);
                    ResizeAnimation resizeAnimation2 = new ResizeAnimation(dateTextView, 0);
                    resizeAnimation2.setDuration(300);
                    dateTextView.startAnimation(resizeAnimation2);
                } else{
                    ResizeAnimation resizeAnimation = new ResizeAnimation(linearLayout, linearLayoutHeight + 50);
                    resizeAnimation.setDuration(300);
                    linearLayout.startAnimation(resizeAnimation);
                    ResizeAnimation resizeAnimation2 = new ResizeAnimation(dateTextView, 50);
                    resizeAnimation2.setDuration(300);
                    dateTextView.startAnimation(resizeAnimation2);
                }
                isExpended = !isExpended;
            }
        });

    }

    public TextMessage getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(TextMessage textMessage) {
        this.textMessage = textMessage;
        this.textView.setText(textMessage.getText());
    }
}