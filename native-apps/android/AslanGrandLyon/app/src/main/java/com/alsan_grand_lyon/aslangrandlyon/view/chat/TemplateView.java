package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.Template;
import com.alsan_grand_lyon.aslangrandlyon.service.LoadImageTask;

/**
 * Created by Nico on 03/05/2017.
 */

public class TemplateView extends FrameLayout {

    private TextView titleTextView = null;
    private TextView subtitleTextView = null;
    private TextView urlTextView = null;
    private ImageView imageView = null;
    private Button button = null;

    private Template template = null;

    public TemplateView(@NonNull Context context) {
        super(context);

        inflate(getContext(), R.layout.template_layout,this);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        subtitleTextView = (TextView) findViewById(R.id.subtitleTextView);
        urlTextView = (TextView) findViewById(R.id.urlTextView);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);

    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(final Template template) {
        this.template = template;
        titleTextView.setText(template.getTitle());
        subtitleTextView.setText(template.getSubtitle());
        urlTextView.setText(template.getUrl());
        LoadImageTask loadImageTask = new LoadImageTask(getContext(),this);
        loadImageTask.execute(template.getImageUrl());
        button.setText(template.getButtonTitle());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(template.getButtonUrl()));
                getContext().startActivity(intent);
            }
        });
    }

    public void showImage(Bitmap bitmap) {
        if(bitmap != null) {
            this.imageView.setVisibility(VISIBLE);
            this.imageView.setImageBitmap(bitmap);
        } else {
            this.imageView.setVisibility(GONE);
        }
    }
}
