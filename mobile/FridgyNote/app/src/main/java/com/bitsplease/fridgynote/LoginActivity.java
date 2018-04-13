package com.bitsplease.fridgynote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.TagHandler;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.Mime;
import com.bitsplease.fridgynote.utils.NfcWrapper;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FN-LoginActivity";

    private NfcWrapper mNfcWrapper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mNfcWrapper = new NfcWrapper(this);
            String readTag = mNfcWrapper.handleIntent(getIntent());
            Log.d(TAG, "Activity start read => " + readTag);
            // this call may end the activity
            if(readTag != null && !BackendConnector.isTagKnown(readTag)) {
                Log.d(TAG, "here");
                TagHandler.launchNewTagActivity(this, readTag);
                finish();
                return;
            } else {
                Log.d(TAG, "here2");
                TagHandler.handleTag(this, readTag);
            }
        } catch (NfcWrapper.NfcWrapperException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if(!sharedPreferences.getString(Constants.KEY_USERNAME, "").equals("")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        Button clickButton = findViewById(R.id.buttonLogin);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO cenas de login genericas

                //Field checks
                EditText email = findViewById(R.id.loginUser);
                EditText pass = findViewById(R.id.loginPass);

                if(TextUtils.isEmpty(email.getText())){
                    email.requestFocus();
                    email.setError("Username field cannot be empty");
                    return;
                }
                if(TextUtils.isEmpty(pass.getText())){
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
            TagHandler.handleTag(this, res);
        }
    }
}
