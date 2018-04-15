package com.bitsplease.fridgynote.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.TagHandler;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.DialogHelper;
import com.bitsplease.fridgynote.utils.NfcWrapper;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class NewTagActivity extends AppCompatActivity {
    private static final String TAG = "FN-NewTagActivity";

    private FloatingActionButton saveButton;
    private TextView tagIdText;
    private TextView tagTitleText;
    private TextView selectListText;
    private Spinner spinner;
    private Spinner selectListSpinner;

    private NfcWrapper mNfcWrapper;
    private String tagId;
    private List<ListNote> listNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        tagId = b.getString(Constants.EXTRA_TAGID, "");

        setContentView(R.layout.activity_new_tag);
        setupUi();

        try {
            mNfcWrapper = new NfcWrapper(this);
        } catch (NfcWrapper.NfcWrapperException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupUi() {
        saveButton = findViewById(R.id.saveButton);
        tagIdText = findViewById(R.id.text_tag_id);
        spinner = findViewById(R.id.tag_type_spinner);
        selectListSpinner = findViewById(R.id.select_list_spinner);
        selectListText = findViewById(R.id.select_list_text);
        tagTitleText = findViewById(R.id.tag_title_text);

        tagIdText.setText(tagId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagTitleText == null || tagTitleText.getText().length() == 0) {
                    DialogHelper.showOkDialog(NewTagActivity.this,
                            "You must choose a title for the NFC tag.");
                    return;
                }
                final String tagId = NewTagActivity.this.tagId;
                Log.d("FN-test", "adding " + tagId);
                String tagTitle = tagTitleText.getText().toString();
                int type = spinner.getSelectedItemPosition();
                switch (type) {
                    case 0:
                        // NOTE TAG
                        boolean noteCreated = BackendConnector.createNoteTag(tagId, tagTitle);
                        if(noteCreated) {
                            Toast.makeText(NewTagActivity.this, "Note tag created", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            DialogHelper.showOkDialog(NewTagActivity.this, "Unable to create note.");
                        }
                        break;
                    case 1:
                        // SHOPPING ITEM TAG
                        boolean shoppingCreated = BackendConnector.createShoppingItemTag(tagId, tagTitle,
                                listNotes.get(selectListSpinner.getSelectedItemPosition()));
                        if(shoppingCreated) {
                            Toast.makeText(NewTagActivity.this, "Shopping item tag created", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            DialogHelper.showOkDialog(NewTagActivity.this, "Unable to create shopping item.");
                        }
                        break;
                    case 2:
                        // REMINDER TAG
                        boolean reminderCreated = BackendConnector.createReminderTag(tagId, tagTitle);
                        if(reminderCreated) {
                            Toast.makeText(NewTagActivity.this, "Reminder tag created", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            DialogHelper.showOkDialog(NewTagActivity.this, "Unable to create reminder.");
                        }
                        break;
                }
                // TODO save tag
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (i == 1) {
                    selectListSpinner.setVisibility(View.VISIBLE);
                    selectListText.setVisibility(View.VISIBLE);
                } else {
                    selectListSpinner.setVisibility(View.GONE);
                    selectListText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectListSpinner.setVisibility(View.GONE);
                selectListText.setVisibility(View.GONE);
            }
        });
        final List<String> list = new ArrayList<>();
        BackendConnector.getNoteTags(this, new BackEndCallback() {
            @Override
            public void tagNotesCallback(List<Note> response) {
                listNotes = new ArrayList<>();
                for(Note n : response) {
                    if(n instanceof ListNote) {
                        listNotes.add((ListNote) n);
                    }
                }

                for (ListNote note : listNotes) {
                    list.add(note.getName());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(NewTagActivity.this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectListSpinner.setAdapter(dataAdapter);
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
            if (!BackendConnector.isTagKnown(getApplicationContext(), res)) {
                tagIdText.setText(res);
            }
        }
    }
}
