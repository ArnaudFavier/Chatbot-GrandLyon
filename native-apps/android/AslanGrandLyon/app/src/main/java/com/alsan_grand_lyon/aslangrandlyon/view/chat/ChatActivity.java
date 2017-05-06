package com.alsan_grand_lyon.aslangrandlyon.view.chat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.model.DataSingleton;
import com.alsan_grand_lyon.aslangrandlyon.model.LocationMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.LocationQuickReplyMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.Message;
import com.alsan_grand_lyon.aslangrandlyon.model.QuickReplyMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.TextMessage;
import com.alsan_grand_lyon.aslangrandlyon.model.User;
import com.alsan_grand_lyon.aslangrandlyon.service.DownloadMessagesTask;
import com.alsan_grand_lyon.aslangrandlyon.service.DownloadMessagesThread;
import com.alsan_grand_lyon.aslangrandlyon.service.MessageHttpResult;
import com.alsan_grand_lyon.aslangrandlyon.service.SendMessageTask;
import com.alsan_grand_lyon.aslangrandlyon.service.SignOutTask;
import com.alsan_grand_lyon.aslangrandlyon.service.Speaker;
import com.alsan_grand_lyon.aslangrandlyon.settings.Settings;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;
import com.alsan_grand_lyon.aslangrandlyon.view.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private AudioManager audio;

    protected LocationManager locationManager;
    private final static int PERMISSION_LOCATION_CODE = 0x2;

    private final static int REQ_CODE_SPEECH_INPUT = 100;
    private final static int CHECK_CODE = 0x1;
    private final int SHORT_DURATION = 1000;
    private Speaker speaker = null;

    private DownloadMessagesThread downloadMessagesThread = null;

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

    private List<Message> messages = null;

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

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        messagesListView = (ListView) findViewById(R.id.messagesListView);
        buttonsLinearLayout = (LinearLayout) findViewById(R.id.buttonsLinearLayout);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);
        microphoneImageView = (ImageView) findViewById(R.id.microphoneImageView);

        messages = new ArrayList<>();
        messages.addAll(DataSingleton.getInstance().getMessages());
        messageAdapter = new MessageAdapter(getApplicationContext(), messages);
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

        ((EditText)findViewById(R.id.messageEditText)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollToBottom();
                return false;
            }
        });
        messagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        messagesListView.setStackFromBottom(true);

        checkTTS();

//        startDownloadMessagesThread();
        refreshMessagesListView();
    }


    private void loadMoreMessage() {
//        if(!loadingMoreMessagesFlag) {
//            loadingMoreMessagesFlag = true;
//            LoadMoreMessagesTask loadMoreMessagesTask = new LoadMoreMessagesTask(ChatActivity.this);
//            loadMoreMessagesTask.execute(limit,DataSingleton.getInstance().getMessages().size());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startDownloadMessagesThread();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkTTS();
//        startDownloadMessagesThread();
    }

    @Override
    protected void onStop() {
        if(speaker != null) {
            speaker.destroy();
        }
//        stopDownloadMessagesThread();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_BACK :
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            stopDownloadMessagesThread();
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
        } else if (id == R.id.nav_settings) {
            showSettings();
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

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
            sendMessage(messageEditText.getText().toString());
            messageEditText.setText("");
            messageEditText.setEnabled(false);
            messageEditText.setEnabled(true);
        }
    }

    private void sendMessage(String text) {
        User user = DataSingleton.getInstance().getUser();
        Message newMessage = new TextMessage(new Date(),user.getServerId(),text);
        newMessage.setUserId(user.getServerId());
        DataSingleton.getInstance().addMessage(newMessage);
        DataSingleton.getInstance().sortMessages();
        SendMessageTask sendMessageTask = new SendMessageTask(this);
        sendMessageTask.execute(newMessage);

        refreshMessagesListView();
        messagesListView.setSelection(messagesListView.getCount());
        scrollToBottom();
    }


    public void messageSent(MessageHttpResult httpResult) {
        if(httpResult.getCode() == 200) {
            User user = DataSingleton.getInstance().getUser();
            DownloadMessagesTask downloadMessagesTask = new DownloadMessagesTask(this);
            downloadMessagesTask.execute(user);
            setAslanIsTyping(true);
        } else if (httpResult.getCode() == 403) {
            Toast toast = Toast.makeText(this,"403 ERROR",Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this,httpResult.getOutput(),Toast.LENGTH_LONG);
            toast.show();
        }
        messageAdapter.notifyDataSetChanged();
    }


    public void refreshMessagesListView() {
        messages.clear();
        int size = DataSingleton.getInstance().getMessages().size();
        messages.addAll(DataSingleton.getInstance().getMessages());
        if(messages.size() != 0) {
            if (messages.get(messages.size() - 1) instanceof QuickReplyMessage) {
                buttonsLinearLayout.removeAllViews();
                QuickReplyMessage message = (QuickReplyMessage) messages.get(messages.size() - 1);
                for (String quickReply : message.getQuickReplies()) {
                    final Button button = new Button(this);
                    button.setText(quickReply);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendMessage(button.getText().toString());
                        }
                    });
                    buttonsLinearLayout.addView(button);
                }
                buttonsLinearLayout.setVisibility(View.VISIBLE);
            } else if (messages.get(messages.size() - 1) instanceof LocationQuickReplyMessage) {
                buttonsLinearLayout.removeAllViews();
                LocationQuickReplyMessage message = (LocationQuickReplyMessage) messages.get(messages.size() - 1);

                final Button button = new Button(this);
                button.setText(getString(R.string.location));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askLocationPermission();
                    }
                });
                buttonsLinearLayout.addView(button);

                buttonsLinearLayout.setVisibility(View.VISIBLE);
            } else {
                buttonsLinearLayout.removeAllViews();
                buttonsLinearLayout.setVisibility(View.GONE);
            }
            messageAdapter.notifyDataSetChanged();
        }
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.you_can_speak));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,1000);
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
                break;
            }
            case PERMISSION_LOCATION_CODE: {
                sendLocation();
                break;
            }
            default:
                break;
        }
    }

    public void readMessages(List<Message> messages){
        String fullText = "";
        for(Message message : messages) {
            if(message instanceof TextMessage) {
                fullText += ((TextMessage)message).getText();
            }
        }
        if(!fullText.isEmpty()) {
            speakOut(fullText);
        }
    }

    private void speakOut(String text) {
        Settings settings = new Settings(this);
        if(settings.isAslanSpeaking()) {
            if (!speaker.isSpeaking()) {
                speaker.speak(text);
                speaker.pause(SHORT_DURATION);
            }
        }
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    private void startDownloadMessagesThread() {
        if(downloadMessagesThread == null) {
            downloadMessagesThread = new DownloadMessagesThread(this);
            downloadMessagesThread.start();
        } else if (downloadMessagesThread.isInterrupted()) {
            downloadMessagesThread.start();
        }
    }

    private void stopDownloadMessagesThread() {
        if(downloadMessagesThread != null && downloadMessagesThread.isAlive()) {
            downloadMessagesThread.interrupt();
        }
        downloadMessagesThread = null;
    }

    public void setAslanIsTyping(boolean isTyping) {
        if(messageAdapter != null) {
            messageAdapter.setAlsanIsTyping(isTyping);
            refreshMessagesListView();
        }
    }

    public void scrollToBottom() {
        messagesListView.setSelection(messageAdapter.getCount() - 1);
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        toast.show();
    }



    private void askLocationPermission(){
        //TODO
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_LOCATION_CODE);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && this.checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && this.checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_LOCATION_CODE);
                } else {
                    sendLocation();
                }
            }

        } else {
            sendLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendLocation();
        }
    }

    public Location getLocation() {
        Location location = null;
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {

            }
            if(location == null) {
                location =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void sendLocation() {
        LocationManager locateManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        boolean enabled = locateManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(enabled) {
            sendLocation(getLocation());
        } else {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivityForResult(intent,PERMISSION_LOCATION_CODE);
        }
    }

    public void sendLocation(Location location) {
        if(location != null) {
            User user = DataSingleton.getInstance().getUser();
            Message newMessage = new LocationMessage(new Date(),user.getServerId(),getString(R.string.location)
                    + " : " + location.getLatitude() + ", " + location.getLongitude(),location.getLatitude(),location.getLongitude());
            newMessage.setUserId(user.getServerId());
            DataSingleton.getInstance().addMessage(newMessage);
            DataSingleton.getInstance().sortMessages();
            SendMessageTask sendMessageTask = new SendMessageTask(this);
            sendMessageTask.execute(newMessage);

            refreshMessagesListView();
            messagesListView.setSelection(messagesListView.getCount());
            scrollToBottom();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
