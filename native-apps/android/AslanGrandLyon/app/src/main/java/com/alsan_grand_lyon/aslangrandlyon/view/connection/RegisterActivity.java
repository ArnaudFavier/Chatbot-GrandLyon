package com.alsan_grand_lyon.aslangrandlyon.view.connection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.service.HttpResult;
import com.alsan_grand_lyon.aslangrandlyon.service.RegisterTask;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;

public class RegisterActivity extends AppCompatActivity {

    private ImageView aslanImageView = null;
    private EditText firstNameEditText = null;
    private EditText lastNameEditText = null;
    private EditText emailEditText = null;
    private EditText passwordEditText = null;
    private EditText confirmPasswordEditText = null;
    private Button registerButton = null;
    private AlertDialog loadingAlertDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        aslanImageView = (ImageView) findViewById(R.id.aslanImageView);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        registerButton = (Button) findViewById(R.id.registerButton);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        findViewById(R.id.main_content).requestFocus();
    }

    private void register() {
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);

        if(checkInputs()) {
            showLoadingDialog();
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            RegisterTask registerTask = new RegisterTask(this);
            registerTask.execute(firstName,lastName,email,password);
        }
    }

    public void registered(HttpResult httpResult) {
        dismissLoadingDialog();
        if(httpResult.getCode() == 200) {
            Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else if (httpResult.getCode() == 403) {
            Toast toast = Toast.makeText(this,getString(R.string.email_already_exists),Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.error_impossible_to_register),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void showLoadingDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);

        View layout = adbInflater.inflate(R.layout.loading_dialog, null);
        adb.setView(layout);


        final TextView loadingMessageTextView = (TextView) layout.findViewById(R.id.loadingMessageTextView);
        loadingMessageTextView.setText(R.string.registration_in_progress);

        adb.setCancelable(false);

        loadingAlertDialog = adb.show();
    }

    private void dismissLoadingDialog() {
        if(loadingAlertDialog != null) {
            loadingAlertDialog.dismiss();
            loadingAlertDialog = null;
        }
    }

    private boolean checkInputs() {
        boolean result = !emailEditText.getText().toString().isEmpty()
                && !passwordEditText.getText().toString().isEmpty()
                && !emailEditText.getText().toString().isEmpty()
                && !passwordEditText.getText().toString().isEmpty()
                && !confirmPasswordEditText.getText().toString().isEmpty();
        if(result) {
            result = passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString());
            if(!result) {
                Toast toast = Toast.makeText(this,getString(R.string.password_are_not_similar),Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this,getString(R.string.all_inputs_have_to_be_filled_in),Toast.LENGTH_LONG);
            toast.show();
        }
        return result;
    }
}
