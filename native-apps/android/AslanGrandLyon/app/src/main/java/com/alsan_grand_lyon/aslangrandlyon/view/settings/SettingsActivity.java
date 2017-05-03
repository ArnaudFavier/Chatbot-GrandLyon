package com.alsan_grand_lyon.aslangrandlyon.view.settings;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.settings.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Switch speakingSwitch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setTitle(R.string.settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        speakingSwitch = (Switch) findViewById(R.id.speakingSwitch);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Settings settings = new Settings(this);
            speakingSwitch.setChecked(settings.isAslanSpeaking());
            speakingSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings settings = new Settings(SettingsActivity.this);
                    settings.setAslanSpeaking(speakingSwitch.isChecked());
                }
            });
        } else {
            speakingSwitch.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                this.finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
