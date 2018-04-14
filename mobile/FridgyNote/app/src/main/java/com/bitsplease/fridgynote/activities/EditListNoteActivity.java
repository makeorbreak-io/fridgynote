package com.bitsplease.fridgynote.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.ListNoteItem;
import com.bitsplease.fridgynote.utils.Constants;

import java.util.List;

public class EditListNoteActivity extends AppCompatActivity {

    private View mContainer;
    private EditText mTitleText;
    private LinearLayout mItemsLayout;
    private View mAddItem;
    private FloatingActionButton mSubmitButton;

    private ListNote mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_note);

        Bundle b = getIntent().getExtras();
        String noteId = b != null ? b.getString(Constants.EXTRA_NOTEID, "") : "";
        mNote = BackendConnector.getListNote(noteId);

        setupUi();
    }

    private void setupUi() {
        mContainer = findViewById(R.id.list_note_container);
        mTitleText = findViewById(R.id.note_title);
        mItemsLayout = findViewById(R.id.list_note_items_layout);
        mAddItem = findViewById(R.id.add_item);
        mSubmitButton = findViewById(R.id.save_note);

        mTitleText.setText(mNote.getName());
        List<ListNoteItem> items = mNote.getItems();
        for (int i = 0; i < items.size(); ++i) {
            addListItem(items.get(i));
        }

        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditListNoteActivity.this);
                builder.setTitle("Add Item");

                final EditText input = new EditText(EditListNoteActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addItem(input.getText().toString());

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void addListItem(final ListNoteItem item) {
        CheckBox box = new CheckBox(this);
        box.setText(item.getText());
        box.setChecked(item.isChecked());
        final int index = mItemsLayout.getChildCount() - 1;

        mItemsLayout.addView(box, index);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                itemCheckChanged(item, index);
            }
        });
    }

    private void itemCheckChanged(ListNoteItem item, int index) {
        // TODO alterar valor na nota
    }

    private void addItem(String itemName) {
        if(itemName == null || itemName.length() == 0) {
            return;
        }

        addListItem(new ListNoteItem(itemName));
        // TODO adicionar item a backend
        Snackbar mySnackbar = Snackbar.make(mContainer, "Item added to shopping list",
                Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    private void submit() {
        //TODO save note to backend
    }
}
