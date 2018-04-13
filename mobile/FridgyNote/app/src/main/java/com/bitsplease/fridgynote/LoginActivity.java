package com.bitsplease.fridgynote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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


}
