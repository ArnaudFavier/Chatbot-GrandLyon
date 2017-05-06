package com.alsan_grand_lyon.aslangrandlyon.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nico on 30/04/2017.
 */

public class Settings {
    private static String PREFS_NAME = "preferences";
    private static String PREFS_IS_ASLAN_SPEAKING = "isAslanSpeaking";

    private SharedPreferences sharedPreferences = null;
    private Context context = null;
    private boolean isAslanSpeaking = false;

    public Settings(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        this.isAslanSpeaking = sharedPreferences.getBoolean(PREFS_IS_ASLAN_SPEAKING, false);
    }

    public boolean isAslanSpeaking() {
        return isAslanSpeaking;
    }

    public void setAslanSpeaking(boolean aslanSpeaking) {
        isAslanSpeaking = aslanSpeaking;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFS_IS_ASLAN_SPEAKING, aslanSpeaking);
        editor.commit();
    }
}
