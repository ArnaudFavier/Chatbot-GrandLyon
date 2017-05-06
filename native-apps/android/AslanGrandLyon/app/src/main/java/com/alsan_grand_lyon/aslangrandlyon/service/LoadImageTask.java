package com.alsan_grand_lyon.aslangrandlyon.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.dao.UserDAO;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.TemplateView;
import com.alsan_grand_lyon.aslangrandlyon.view.launch.SplashActivity;

import java.io.InputStream;


/**
 * Created by Nico on 24/04/2017.
 */

public class LoadImageTask extends AsyncTask<String, String, Bitmap> {

    private Context context;
    private TemplateView templateView;

    public LoadImageTask(Context context, TemplateView templateView) {
        this.context = context;
        this.templateView = templateView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap bitmap =  FileManager.loadImage(context, url);;
        try {
            if(bitmap == null) {
                // Download Image from URL
                InputStream input = new java.net.URL(url).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);

                FileManager.saveBitmap(context, bitmap, url);
            }

        } catch (Exception e) {

        }
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        templateView.showImage(result);
    }

}