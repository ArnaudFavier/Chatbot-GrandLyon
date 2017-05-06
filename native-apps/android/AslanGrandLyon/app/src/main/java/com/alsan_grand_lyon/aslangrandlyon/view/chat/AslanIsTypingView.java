package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;
import com.alsan_grand_lyon.aslangrandlyon.view.RoundImage;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Nico on 21/04/2017.
 */

public class AslanIsTypingView extends FrameLayout {

    private LinearLayout linearLayout = null;
    private ImageView imageView = null;
    private boolean isExpended = false;
    private int linearLayoutHeight = -1;
    private DilatingDotsProgressBar dilatingDotsProgressBar;

    public AslanIsTypingView(@NonNull Context context) {
        super(context);

        inflate(getContext(), R.layout.aslan_is_typing_layout,this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView = (ImageView) findViewById(R.id.imageView);
        dilatingDotsProgressBar = (DilatingDotsProgressBar) findViewById(R.id.progress);
        dilatingDotsProgressBar.setGrowthSpeed(750);
        dilatingDotsProgressBar.showNow();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.aslan);
        RoundImage roundedImage = new RoundImage(bitmap);
        imageView.setImageDrawable(roundedImage);

        isExpended = false;

        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayoutHeight == -1) {
                    linearLayoutHeight = linearLayout.getHeight();
                }
                if(isExpended) {
                    ResizeAnimation resizeAnimation = new ResizeAnimation(linearLayout, linearLayoutHeight
                    );
                    resizeAnimation.setDuration(300);
                    linearLayout.startAnimation(resizeAnimation);
                } else{
                    ResizeAnimation resizeAnimation = new ResizeAnimation(linearLayout, linearLayoutHeight + 50);
                    resizeAnimation.setDuration(300);
                    linearLayout.startAnimation(resizeAnimation);
                }
                isExpended = !isExpended;
            }
        });

    }


}
