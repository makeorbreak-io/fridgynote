package com.bitsplease.fridgynote.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.ListNoteItem;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.DialogHelper;
import com.bitsplease.fridgynote.utils.ImageLoader;
import com.bitsplease.fridgynote.utils.PreferenceUtils;
import com.bitsplease.fridgynote.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListNoteActivity extends AppCompatActivity implements BackEndCallback {

    private View mContainer;
    private TextView mTitleText;
    private LinearLayout mItemsLayout;
    private View mAddItem;
    private Button mShareButton;

    private ListNote mNote;
    private String mNoteId;

    private ListNoteItem toDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_note);

        Bundle b = getIntent().getExtras();
        mNoteId = b != null ? b.getString(Constants.EXTRA_NOTEID, "") : "";

        BackendConnector.getNoteTags(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_sync:
                BackendConnector.getNoteTags(this, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupUi() {
        mContainer = findViewById(R.id.list_note_container);
        mTitleText = findViewById(R.id.note_title_text);
        mItemsLayout = findViewById(R.id.list_note_items_layout);
        mAddItem = findViewById(R.id.add_item);
        mShareButton = findViewById(R.id.share_button);

        while (mItemsLayout.getChildCount() > 1) {
            mItemsLayout.removeViewAt(0);
        }
        mTitleText.setText(mNote.getName());
        List<ListNoteItem> items = mNote.getItems();
        for (int i = 0; i < items.size(); ++i) {
            addListItem(items.get(i));
        }

        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListNoteActivity.this);
                builder.setTitle("Add Item");

                final EditText input = new EditText(ListNoteActivity.this);
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

        Map<String, String> users = mNote.getAllUsersExcept(PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""));
        final List<String> keys = new ArrayList<>(users.keySet());
        for(int i = 0; i < 5; ++i) {
            findViewById(getViewParent(getUserSharedId(i))).setVisibility(View.GONE);
        }
        for(int i = 0; i < keys.size(); ++i) {
            TextView v = findViewById(getUserSharedId(i));
            View parent = findViewById(getViewParent(getUserSharedId(i)));
            parent.setVisibility(View.VISIBLE);
            final int index = i;
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogHelper.showOkDialog(ListNoteActivity.this, keys.get(index));
                }
            });
            v.setText("" + keys.get(i).toUpperCase().charAt(0));
        }

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListNoteActivity.this);
                builder.setTitle("Share note with user");

                final EditText input = new EditText(ListNoteActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("User name");
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNote.getSharedUsers().put(String.valueOf(input.getText()), "");
                        BackendConnector.updateListNote(ListNoteActivity.this, mNote);
                        setupUi();
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
    }

    private int getUserSharedId(int index) {
        switch (index) {
            case 0:
                return R.id.share_user_1;
            case 1:
                return R.id.share_user_2;
            case 2:
                return R.id.share_user_3;
            case 3:
                return R.id.share_user_4;
            case 4:
                return R.id.share_user_5;
        }
        return 0;
    }

    private int getViewParent(int viewId) {
        switch (viewId) {
            case R.id.share_user_1:
                return R.id.share_user_1_parent;
            case R.id.share_user_2:
                return R.id.share_user_2_parent;
            case R.id.share_user_3:
                return R.id.share_user_3_parent;
            case R.id.share_user_4:
                return R.id.share_user_4_parent;
            case R.id.share_user_5:
                return R.id.share_user_5_parent;
        }
        return 0;
    }

    private void addListItem(final ListNoteItem item) {
        CheckBox box = new CheckBox(this);
        box.setText(item.getText());
        box.setChecked(item.isChecked());
        final int index = mItemsLayout.getChildCount() - 1;

        box.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toDelete = item;
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ListNoteActivity.this);
                builder.setMessage("Are you sure you want to delete this note?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });

        mItemsLayout.addView(box, index);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                itemCheckChanged(item, index);
            }
        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mNote.getItems().remove(toDelete);
            BackendConnector.updateListNote(ListNoteActivity.this, mNote);
        }
    };

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

    @Override
    public void tagNotesCallback(List<Note> response) {
        for(Note n : response) {
            if(!(n instanceof ListNote)) {
                continue;
            }
            ListNote note = (ListNote) n;
            mNote = note;
            if(note.getId().equals(mNoteId)) {
                break;
            }
        }
        setupUi();
    }
}
