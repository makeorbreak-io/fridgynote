package com.bitsplease.fridgynote.activities;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.ListNoteItem;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.Reminders;
import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.TagHandler;
import com.bitsplease.fridgynote.fragments.NoteGroupFragment;
import com.bitsplease.fridgynote.fragments.ShoppingItemsFragment;
import com.bitsplease.fridgynote.fragments.MemoFragment;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.NfcWrapper;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "FN-MainActivity";

    private NfcWrapper mNfcWrapper;

    private FragmentManager fragmentManager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    NoteGroupFragment notesFragment = new NoteGroupFragment();
                    fragmentTransaction.replace(android.R.id.content,  notesFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    ShoppingItemsFragment shoppingFragment = new ShoppingItemsFragment();
                    fragmentTransaction.replace(android.R.id.content,  shoppingFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    MemoFragment memoFragment = new MemoFragment();
                    fragmentTransaction.replace(android.R.id.content,  memoFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NoteGroupFragment notesFragment = new NoteGroupFragment();
        fragmentTransaction.replace(android.R.id.content,  notesFragment);

        fragmentTransaction.commit();

        String readTagContent = null;
        try {
            mNfcWrapper = new NfcWrapper(this);
            String readTag = mNfcWrapper.handleIntent(getIntent());
            Log.d(TAG, "Activity start read => " + readTag);
            TagHandler.handleTag(getApplicationContext(), readTag);
        } catch (NfcWrapper.NfcWrapperException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BackendConnector.getNoteTags(this, new BackEndCallback() {
            @Override
            public void tagNotesCallback(List<Note> response) {
                Log.d("FN-test", "" + response.size());
            }
        });
    }

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
            String readTag = mNfcWrapper.handleIntent(intent);
            Log.d(TAG, "Activity active read => " + readTag);
            TagHandler.handleTag(getApplicationContext(), readTag);
        }
    }

}
