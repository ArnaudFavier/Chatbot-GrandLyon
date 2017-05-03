package com.alsan_grand_lyon.aslangrandlyon.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Nico on 23/12/2016.
 */

public class FileManager {


    public static void saveBitmap(Context context, Bitmap bitmap, String url) {
        FileOutputStream out = null;
        try {
            File cacheDir = context.getCacheDir().getAbsoluteFile();
            File imagesDir = new File(cacheDir.getAbsolutePath() + "/images");
            if(!imagesDir.exists())
            {
                imagesDir.mkdir();
            }
            out = new FileOutputStream(imagesDir.getAbsoluteFile() + "/" + Integer.toString(url.hashCode()));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeUnusedImages(Context context, Set<String> images) {
        File cacheDir = context.getCacheDir().getAbsoluteFile();
        File imagesDir = new File(cacheDir.getAbsolutePath() + "/images");
        if(imagesDir.exists())
        {
           for(File file : imagesDir.listFiles()) {
               if(!images.contains(file.getName())) {
                   file.delete();
               }
           }
        }
    }

    public static boolean imageExists(Context context, String url) {
        File cacheDir = context.getCacheDir().getAbsoluteFile();
        File imagesDir = new File(cacheDir.getAbsolutePath() + "/images");
        return new File(imagesDir + "/" + Integer.toString(url.hashCode())).exists();
    }

    public static Bitmap loadImage(Context context, String url) {
        File cacheDir = context.getCacheDir().getAbsoluteFile();
        File imagesDir = new File(cacheDir.getAbsolutePath() + "/images");
        if(imageExists(context,url)){
            return BitmapFactory.decodeFile(imagesDir + "/" + Integer.toString(url.hashCode()));
        }
        return null;
    }

}
