package com.bitsplease.fridgynote.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TagNotesActivity extends AppCompatActivity {


    String toDelete=null;
    Note toDeleteClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_notes);

        Bundle b = getIntent().getExtras();
        final String noteId = b != null ? b.getString(Constants.EXTRA_TAGID, "") : "";

        BackendConnector.getNoteTags(this, new BackEndCallback() {
            @Override
            public void tagNotesCallback(List<Note> response) {
                List<Note> toRemove = new ArrayList<>();
                for (Note n : response) {
                    if (!n.getTagId().equals(noteId)) {
                        //response.remove(n);
                        toRemove.add(n);
                    } else if (noteId.equals("unassigned") && n.getTagId() != null) {
                        //response.remove(n);
                        toRemove.add(n);
                    }
                }
                response.removeAll(toRemove);
                createNoteList(response);
            }
        });

    }

    public void createNoteList(final List<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            String title = "";
            final boolean isTextNote;

            int id = getResources().getIdentifier("card" + (i + 1), "id", this.getPackageName());
            CardView cardView= findViewById(id);
            //registerForContextMenu(cardView);
            cardView.setLongClickable(true);

            if (notes.get(i) instanceof TextNote) {
                title = ((TextNote) notes.get(i)).getTitle();
                isTextNote = true;
            } else if (notes.get(i) instanceof ListNote) {
                title = ((ListNote) notes.get(i)).getName();
                isTextNote = false;
            } else {
                isTextNote = true;
            }

            ((TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(0)).setText(title);
            cardView.setVisibility(View.VISIBLE);
            final String noteId = notes.get(i).getId();
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    if (isTextNote) {
                        intent = new Intent(TagNotesActivity.this, TextNoteActivity.class);
                    } else {
                        intent = new Intent(TagNotesActivity.this, ListNote.class);
                    }
                    intent.putExtra(Constants.EXTRA_NOTEID, noteId);
                    startActivity(intent);
                }
            });
            final int finalI = i;
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    toDelete = noteId;
                    toDeleteClass = notes.get(finalI);
                    AlertDialog.Builder builder = new AlertDialog.Builder(TagNotesActivity.this);
                    builder.setMessage("Are you sure you want to delete this note?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return true;
                }
            });
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    if(toDelete != null) {
                        if (toDeleteClass instanceof TextNote) {
                            BackendConnector.deleteTextNote(TagNotesActivity.this, toDelete);
                        } else if (toDeleteClass instanceof ListNote) {
                            BackendConnector.deleteListNote(TagNotesActivity.this, toDelete);
                        }
                        recreate();
                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };



}
