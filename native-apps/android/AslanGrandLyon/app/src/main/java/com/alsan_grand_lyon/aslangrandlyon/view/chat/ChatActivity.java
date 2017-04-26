package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.Profile;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.service.HttpResult;
import com.alsan_grand_lyon.aslangrandlyon.service.MessageHttpResult;
import com.alsan_grand_lyon.aslangrandlyon.service.SendMessageTask;
import com.alsan_grand_lyon.aslangrandlyon.service.SignOutTask;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView messagesListView = null;
    private LinearLayout buttonsLinearLayout = null;
    private EditText messageEditText = null;
    private ImageView sendImageView = null;
    private MessageAdapter messageAdapter = null;
    private AlertDialog loadingAlertDialog = null;
    private boolean signingOutFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        messagesListView = (ListView) findViewById(R.id.messagesListView);
        buttonsLinearLayout = (LinearLayout) findViewById(R.id.buttonsLinearLayout);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);

        //TODO
        messageAdapter = new MessageAdapter(getApplicationContext(), DataSingleton.getInstance().getMessages());
        messagesListView.setAdapter(messageAdapter);
        messagesListView.setDivider(null);


        buttonsLinearLayout.removeAllViews();
        buttonsLinearLayout.setVisibility(View.GONE);

        messagesListView.setSelection(messagesListView.getCount()-1);

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLoadingDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);

        View layout = adbInflater.inflate(R.layout.loading_dialog, null);
        adb.setView(layout);

        final TextView loadingMessageTextView = (TextView) layout.findViewById(R.id.loadingMessageTextView);
        loadingMessageTextView.setText(R.string.sign_out_in_progress);

        adb.setCancelable(false);

        loadingAlertDialog = adb.show();
    }

    private void signOut() {
        if(!signingOutFlag) {
            signingOutFlag = true;
            showLoadingDialog();
            SignOutTask signInTask = new SignOutTask(this);
            signInTask.execute();
        }
    }

    public void signedOut(int result) {
        loadingAlertDialog.dismiss();
        if(result == 0) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast toast = Toast.makeText(this,getString(R.string.error_when_sign_out),Toast.LENGTH_LONG);
            toast.show();
            signingOutFlag = false;
        }
    }

    private void sendMessage() {
        //TODO tester quel type de message, pour l'instant seulement TextMessage
        if(messageEditText.getText().toString() != null) {
            User user = DataSingleton.getInstance().getUser();
            Message newMessage = new TextMessage(new Date(),user.getServerId(),messageEditText.getText().toString());
            newMessage.setUserId(user.getServerId());
            DataSingleton.getInstance().addMessage(newMessage);
            DataSingleton.getInstance().sortMessages();
            refreshMessagesListView();
            SendMessageTask sendMessageTask = new SendMessageTask(this);
            sendMessageTask.execute(newMessage);
        }
    }

    public void messageSent(MessageHttpResult httpResult) {
        if(httpResult.getCode() == 200) {
            Toast toast = Toast.makeText(this,"Message sent",Toast.LENGTH_LONG);
            toast.show();
        } else if (httpResult.getCode() == 403) {
            Toast toast = Toast.makeText(this,"403 ERROR",Toast.LENGTH_LONG);
            toast.show();
        } else {
            System.out.println(httpResult.getCode() + " " + httpResult.getOutput());
            Toast toast = Toast.makeText(this,httpResult.getOutput(),Toast.LENGTH_LONG);
            toast.show();
        }
        messageAdapter.notifyDataSetChanged();
    }

    public void refreshMessagesListView() {
        messageAdapter.notifyDataSetChanged();
    }

}
