package com.bitsplease.fridgynote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bitsplease.fridgynote.utils.NfcWrapper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FN-MainActivity";

    private NfcWrapper mNfcWrapper;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String readTagContent = null;
        try {
            mNfcWrapper = new NfcWrapper(this);
            readTagContent = mNfcWrapper.handleIntent(getIntent());
            Log.d(TAG, "start read " + readTagContent);
        } catch (NfcWrapper.NfcWrapperException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
            Log.d(TAG, "handle " + res);
        }
    }

}
