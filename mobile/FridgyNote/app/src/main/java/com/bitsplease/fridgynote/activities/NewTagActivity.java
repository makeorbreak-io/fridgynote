package com.bitsplease.fridgynote.activities;

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
import com.bitsplease.fridgynote.controller.TagHandler;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.NfcWrapper;

import java.util.ArrayList;
import java.util.List;

public class NewTagActivity extends AppCompatActivity {

    private FloatingActionButton saveButton;
    private TextView tagIdText;
    private TextView selectListText;
    private Spinner spinner;
    private Spinner selectListSpinner;

    private NfcWrapper mNfcWrapper;
    private String tagId;

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

        tagIdText.setText(tagId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO check values are correct and save tag
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

        // TODO get available lists
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectListSpinner.setAdapter(dataAdapter);
    }
}
