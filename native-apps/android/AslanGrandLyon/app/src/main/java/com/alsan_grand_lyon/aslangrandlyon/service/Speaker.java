package com.alsan_grand_lyon.aslangrandlyon.service;

/**
 * Created by Nico on 30/04/2017.
 */

import java.util.Locale;

import android.content.Context;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Speaker implements OnInitListener {
    private static Locale language = Locale.FRANCE;

    private static TextToSpeech tts;
    private boolean isReady = false;

    public Speaker(Context context){
        tts = new TextToSpeech(context, this);
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.5f);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            tts.setLanguage(language);
            isReady = true;
        } else{
            isReady = false;
        }
    }

    public void speak(String text){
        if(isReady) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    public void setSpeedRate(float speechrate) {
        tts.setSpeechRate(speechrate);
    }

    public void setPitchRate(float pitchrate) {
        tts.setPitch(pitchrate);
    }

    public boolean isSpeaking() {
        return tts.isSpeaking();
    }

    public void pause(int duration){
        tts.playSilentUtterance(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void stop() {
        tts.stop();
    }

    public void destroy() {
        tts.shutdown();
    }
}