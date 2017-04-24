package com.alsan_grand_lyon.aslangrandlyon.service;

import android.os.AsyncTask;

import com.alsan_grand_lyon.aslangrandlyon.R;
import com.alsan_grand_lyon.aslangrandlyon.view.connection.RegisterActivity;

/**
 * Created by Nico on 24/04/2017.
 */

public class RegisterTask extends AsyncTask<String, String, PostResult> {
    private RegisterActivity registerActivity;

    public RegisterTask(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected PostResult doInBackground(String... params) {
        String firstName = params[0];
        String lastName = params[1];
        String email = params[2];
        String password = params[3];

        String url = registerActivity.getString(R.string.server_url) + registerActivity.getString(R.string.server_register);
        return CallAPI.register(url,firstName,lastName,email,password);
    }


    @Override
    protected void onPostExecute(PostResult result) {
        registerActivity.register(result);
    }

}