package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.service.LoadMoreMessagesTask;
import com.alsan_grand_lyon.aslangrandlyon.service.MessageHttpResult;
import com.alsan_grand_lyon.aslangrandlyon.service.SendMessageTask;
import com.alsan_grand_lyon.aslangrandlyon.service.SignOutTask;
import com.alsan_grand_lyon.aslangrandlyon.service.Speaker;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQ_CODE_SPEECH_INPUT = 100;
    private final static int CHECK_CODE = 0x1;
    private final int SHORT_DURATION = 1000;
    private Speaker speaker = null;

    private ListView messagesListView = null;
    private LinearLayout buttonsLinearLayout = null;
    private EditText messageEditText = null;
    private ImageView sendImageView = null;
    private ImageView microphoneImageView = null;
    private MessageAdapter messageAdapter = null;
    private AlertDialog loadingAlertDialog = null;
    private boolean signingOutFlag = false;
    private int limit = 2;
    private boolean loadingMoreMessagesFlag = false;

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
        microphoneImageView = (ImageView) findViewById(R.id.microphoneImageView);

        //TODO
        messageAdapter = new MessageAdapter(getApplicationContext(), DataSingleton.getInstance().getMessages());
        messagesListView.setAdapter(messageAdapter);
        messagesListView.setDivider(null);
        messagesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0) {
                    loadMoreMessage();
                }
            }
        });

        buttonsLinearLayout.removeAllViews();
        buttonsLinearLayout.setVisibility(View.GONE);
        
        messagesListView.setSelection(messagesListView.getCount());

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        microphoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        checkTTS();
    }


    private void loadMoreMessage() {
        if(!loadingMoreMessagesFlag) {
            loadingMoreMessagesFlag = true;
            LoadMoreMessagesTask loadMoreMessagesTask = new LoadMoreMessagesTask(ChatActivity.this);
            loadMoreMessagesTask.execute(limit,DataSingleton.getInstance().getMessages().size());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkTTS();
    }

    @Override
    protected void onStop() {
        if(speaker != null) {
            speaker.destroy();
        }
        super.onStop();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        if(messageEditText.getText().toString() != null && !messageEditText.getText().toString().isEmpty()) {
            User user = DataSingleton.getInstance().getUser();
            Message newMessage = new TextMessage(new Date(),user.getServerId(),messageEditText.getText().toString());
            newMessage.setUserId(user.getServerId());
            DataSingleton.getInstance().addMessage(newMessage);
            DataSingleton.getInstance().sortMessages();
            SendMessageTask sendMessageTask = new SendMessageTask(this);
            sendMessageTask.execute(newMessage);
            messageEditText.setText("");
            messageEditText.setEnabled(false);
            messageEditText.setEnabled(true);
            refreshMessagesListView();
            messagesListView.setSelection(messagesListView.getCount());
        }
    }

    public void messageSent(MessageHttpResult httpResult) {
        //TODO faire differement (progress bar sur le message ...)
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

    public void moreMessagesLoaded(int numberOfNewMessages) {
        if(numberOfNewMessages < limit) {
            messagesListView.setOnScrollListener(null);
            messageAdapter.setCanLoadMoreMessages(false);
        } else {
            this.loadingMoreMessagesFlag = false;
        }


        refreshMessagesListView();
        if(messagesListView.getFirstVisiblePosition() == 0) {
            messagesListView.setSelection(numberOfNewMessages + 1);
        } else {
            messagesListView.setSelection(messagesListView.getFirstVisiblePosition() + numberOfNewMessages);
        }

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.you_can_speake));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.sorry_no_voice_input_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> buffer = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String result = buffer.get(0);
                    //TODO si texte vide
                    messageEditText.setText(result);
                }
                break;
            }
            case CHECK_CODE: {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    speaker = new Speaker(this);
                } else {
                    Intent install = new Intent();
                    install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(install);
                }
            }
            default:
                break;
        }
    }

    private void speakOut(String text) {
        if(!speaker.isSpeaking()) {
            speaker.speak(text);
            speaker.pause(SHORT_DURATION);
        }
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }
}
