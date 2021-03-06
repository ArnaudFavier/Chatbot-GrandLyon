package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Nico on 21/04/2017.
 */

public class UserTextMessageView extends FrameLayout {

    private LinearLayout linearLayout = null;
    private TextMessage message = null;
    private TextView textView = null;
    private TextView dateTextView = null;
    private ProgressBar progressBar = null;
    private boolean isExpended = false;
    private int linearLayoutHeight = -1;

    public UserTextMessageView(@NonNull Context context) {
        super(context);

        inflate(getContext(), R.layout.user_text_message_layout,this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        textView = (TextView) findViewById(R.id.textView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(GONE);
        isExpended = false;
        dateTextView.setHeight(0);

        linearLayout.setOnClickListener(new OnClickListener() {
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

    public TextMessage getMessage() {
        return message;
    }

    public void setMessage(TextMessage message) {
        this.message = message;
        this.textView.setText(message.getText());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.dateTextView.setText(dateFormat.format(this.message.getDate()));
        if(message.getServerId() != null && !message.getServerId().isEmpty()) {
            this.progressBar.setVisibility(GONE);
        } else {
            this.progressBar.setVisibility(VISIBLE);
        }
    }
}