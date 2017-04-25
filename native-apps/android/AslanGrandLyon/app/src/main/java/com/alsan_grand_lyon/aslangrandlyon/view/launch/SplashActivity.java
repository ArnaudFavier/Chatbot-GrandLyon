package com.alsan_grand_lyon.aslangrandlyon.view.launch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.service.LaunchingTask;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LaunchingTask launchingTask = new LaunchingTask(this);
        launchingTask.execute();
    }

    public void loaded(User user) {
        if(user == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        }
        finish();
    }
}