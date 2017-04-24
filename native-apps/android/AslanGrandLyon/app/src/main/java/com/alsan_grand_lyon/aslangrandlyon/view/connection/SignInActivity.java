package com.alsan_grand_lyon.aslangrandlyon.view.connection;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.service.PostResult;
import com.alsan_grand_lyon.aslangrandlyon.service.SignInTask;
import com.alsan_grand_lyon.aslangrandlyon.view.chat.ChatActivity;

/**
 * Created by Nico on 24/04/2017.
 */


public class SignInActivity extends AppCompatActivity {

    private ImageView aslanImageView = null;
    private EditText emailEditText = null;
    private EditText passwordEditText = null;
    private Button signInButton = null;
    private TextView resgisterTextView = null;
    private AlertDialog loadingAlertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        aslanImageView = (ImageView) findViewById(R.id.aslanImageView);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        signInButton = (Button) findViewById(R.id.signInButton);
        resgisterTextView = (TextView) findViewById(R.id.resgisterTextView);


        resgisterTextView.setText(fromHtml("<u>" + getString(R.string.register) + "</u>"));
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInButtonClick();
            }
        });

        resgisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public void onSignInButtonClick() {
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(signInButton.getWindowToken(), 0);

        if(checkInputs()) {
            showLoadingDialog();
            SignInTask signInTask = new SignInTask(SignInActivity.this);
            signInTask.execute(emailEditText.getText().toString(), passwordEditText.getText().toString());
        }
    }

    public void signIn(PostResult postResult) {
        dismissLoadingDialog();

        if(postResult.getCode() == 200) {
            Intent intent = new Intent(SignInActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else if (postResult.getCode() == 403) {
            Toast toast = Toast.makeText(this,getString(R.string.ckecks_logins),Toast.LENGTH_LONG);
            toast.show();
            passwordEditText.setText("");
        } else {
            Toast toast = Toast.makeText(this,getString(R.string.impossible_to_connect),Toast.LENGTH_LONG);
            toast.show();
            passwordEditText.setText("");
        }
    }

    private void showLoadingDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);

        View layout = adbInflater.inflate(R.layout.loading_dialog, null);
        adb.setView(layout);

        final TextView loadingMessageTextView = (TextView) layout.findViewById(R.id.loadingMessageTextView);
        loadingMessageTextView.setText(R.string.connection_in_progress);

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
                && !passwordEditText.getText().toString().isEmpty();
        if(!result) {
            Toast toast = Toast.makeText(SignInActivity.this, getString(R.string.please_enter_logins), Toast.LENGTH_LONG);
            toast.show();
        }
        return result;
    }

}
