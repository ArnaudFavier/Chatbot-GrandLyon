package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.SignInActivity;

/**
 * Created by Nico on 24/04/2017.
 */

public class SignInTask extends AsyncTask<String, String, PostResult> {
    private SignInActivity signInActivity;

    public SignInTask(SignInActivity signInActivity) {
        this.signInActivity = signInActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected PostResult doInBackground(String... params) {
        String email = params[0];
        String password = params[1];
        String url = signInActivity.getString(R.string.server_url) + signInActivity.getString(R.string.server_sign_in);
        return CallAPI.signIn(url,email,password);
    }


    @Override
    protected void onPostExecute(PostResult result) {
        signInActivity.signIn(result);
    }

}