package com.alsan_grand_lyon.aslangrandlyon.view.launch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.service.GetMessagesTask;
import com.alsan_grand_lyon.aslangrandlyon.service.HttpResult;
import com.alsan_grand_lyon.aslangrandlyon.service.LoadUserTask;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;
import com.alsan_grand_lyon.aslangrandlyon.service.interfaces.LoadingMessageActivity;

public class SplashActivity extends AppCompatActivity  implements LoadingMessageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadUserTask loadUserTask = new LoadUserTask(this);
        loadUserTask.execute();
    }

    public void userLoaded(User user) {
        if(user == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            GetMessagesTask getMessagesTask = new GetMessagesTask(this,this);
            getMessagesTask.execute(user);
        }

    }


    @Override
    public void messagesLoaded(HttpResult httpResult) {
        if(httpResult.getCode() == 200) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        finish();
    }
}