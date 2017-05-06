package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.Template;
import com.alsan_grand_lyon.aslangrandlyon.model.TemplatesMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;
import com.alsan_grand_lyon.aslangrandlyon.view.RoundImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Nico on 21/04/2017.
 */

public class AslanTemplatesMessageView extends FrameLayout {

    private LinearLayout linearLayout = null;
    private LinearLayout templatesLinearLayout = null;
    private TemplatesMessage message = null;
    private TextView textView = null;
    private TextView dateTextView = null;
    private ImageView imageView = null;
    private Button showAllButton = null;
    private boolean isExpended = false;
    private boolean isAllShown = false;
    private int linearLayoutHeight = -1;

    public AslanTemplatesMessageView(@NonNull Context context) {
        super(context);

        inflate(getContext(), R.layout.aslan_templates_message_layout,this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        templatesLinearLayout = (LinearLayout) findViewById(R.id.templatesLinearLayout);
        textView = (TextView) findViewById(R.id.textView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        imageView = (ImageView) findViewById(R.id.imageView);
        showAllButton = (Button) findViewById(R.id.showAllButton);

        isExpended = false;
        dateTextView.setHeight(0);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.aslan);
        RoundImage roundedImage = new RoundImage(bitmap);
        imageView.setImageDrawable(roundedImage);

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

    public TemplatesMessage getMessage() {
        return message;
    }

    public void setMessage(TemplatesMessage templatesMessage) {
        this.message = templatesMessage;
        this.textView.setText(message.getText());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.dateTextView.setText(dateFormat.format(this.message.getDate()));
        for(int i = 0; i < message.getTemplates().size() && i < 5; i++) {
            TemplateView templateView = new TemplateView(getContext());
            templateView.setTemplate(message.getTemplates().get(i));
            templatesLinearLayout.addView(templateView);
        }
        if(message.getTemplates().size() < 5) {
            showAllButton.setVisibility(GONE);
        } else {
            showAllButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAllShown) {
                        templatesLinearLayout.removeAllViews();
                        for(int i = 0; i < message.getTemplates().size() && i < 5; i++) {
                            TemplateView templateView = new TemplateView(getContext());
                            templateView.setTemplate(message.getTemplates().get(i));
                            templatesLinearLayout.addView(templateView);
                        }
                        showAllButton.setText(getContext().getString(R.string.show_more));
                    } else {
                        templatesLinearLayout.removeAllViews();
                        for(int i = 0; i < message.getTemplates().size(); i++) {
                            TemplateView templateView = new TemplateView(getContext());
                            templateView.setTemplate(message.getTemplates().get(i));
                            templatesLinearLayout.addView(templateView);
                        }
                        showAllButton.setText(getContext().getString(R.string.show_less));
                    }
                    isAllShown = !isAllShown;
                }
            });
        }

    }
}