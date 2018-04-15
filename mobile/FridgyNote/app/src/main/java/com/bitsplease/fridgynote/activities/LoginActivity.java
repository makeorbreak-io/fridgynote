package com.bitsplease.fridgynote.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bitsplease.fridgynote.R;

import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.TagHandler;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.NfcWrapper;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FN-LoginActivity";

    private NfcWrapper mNfcWrapper;
    public  SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharedPreferences prefs = getApplicationContext().getSharedPreferences("fridgynote-prefs", Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceUtils.setPreferences(prefs);

        try {
            mNfcWrapper = new NfcWrapper(this);
            String readTag = mNfcWrapper.handleIntent(getIntent());
            Log.d(TAG, "Activity start read => " + readTag);
            // this call may end the activity
            if(readTag != null && !BackendConnector.isTagKnown(getApplicationContext(), readTag)) {
                TagHandler.launchNewTagActivity(this, readTag);
                finish();
                return;
            } else {
                if(TagHandler.handleTag(getApplicationContext(), readTag)) {
                    finish();
                    return;
                }
            }
        } catch (NfcWrapper.NfcWrapperException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        sharedPreferences = PreferenceUtils.getPrefs();
        if (!sharedPreferences.getString(Constants.KEY_USERNAME, "").equals("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        Button clickButton = findViewById(R.id.buttonLogin);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //Field checks
                EditText email = findViewById(R.id.loginUser);
                EditText pass = findViewById(R.id.loginPass);

                if (TextUtils.isEmpty(email.getText())) {
                    email.requestFocus();
                    email.setError("Username field cannot be empty");
                    return;
                }
                if (TextUtils.isEmpty(pass.getText())) {
                    pass.requestFocus();
                    pass.setError("Password field cannot be empty");
                    return;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_USERNAME, String.valueOf(email.getText()));
                editor.putString(Constants.KEY_PASSWORD, String.valueOf(pass.getText()));
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcWrapper != null) {
            mNfcWrapper.setupForegroundDispatch();
        }
    }

    @Override
    protected void onPause() {
        if (mNfcWrapper != null) {
            mNfcWrapper.stopForegroundDispatch();
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mNfcWrapper != null) {
            String res = mNfcWrapper.handleIntent(intent);
            Log.d(TAG, "Activity active read => " + res);
            TagHandler.handleTag(getApplicationContext(), res);
        }
    }
}
