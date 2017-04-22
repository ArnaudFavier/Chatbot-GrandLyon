package com.alsan_grand_lyon.aslangrandlyon.view.connection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alsan_grand_lyon.aslangrandlyon.R;

public class SignUpActivity extends AppCompatActivity {

    private ImageView aslanImageView = null;
    private EditText firstNameEditText = null;
    private EditText lastNameEditText = null;
    private EditText emailEditText = null;
    private EditText passwordEditText = null;
    private EditText conformPasswordEditText = null;
    private Button signUpButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        aslanImageView = (ImageView) findViewById(R.id.aslanImageView);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        conformPasswordEditText = (EditText) findViewById(R.id.conformPasswordEditText);
        signUpButton = (Button) findViewById(R.id.signUpButton);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.main_content).requestFocus();
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
}
