package com.bitsplease.fridgynote.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagNotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_notes);
        //TextNote test = new TextNote("123","dada","xrdcfvgbshd",new ArrayList<String>());
        //createNoteList(Arrays.asList(test));

        Bundle b = getIntent().getExtras();
        final String noteId = b != null ? b.getString(Constants.EXTRA_TAGID, "") : "";

        BackendConnector.getNoteTags(this, new BackEndCallback() {
            @Override
            public void tagNotesCallback(List<Note> response) {
                for (Note n:response) {
                    if(!n.getTagId().equals(noteId)){
                        response.remove(n);
                    }else if(noteId.equals("unassigned") && n.getTagId() != null){
                        response.remove(n);
                    }
                }
                createNoteList(response);
            }
        });

    }

    public void createNoteList(List<Note> notes){
        for(int i = 0; i< notes.size();i++){
            String title = "";
            int id = getResources().getIdentifier("card" + (i + 1), "id", this.getPackageName());
            CardView cardView= findViewById(id);

            if(notes.get(i) instanceof TextNote){
                title=((TextNote) notes.get(i)).getTitle();
            }else if(notes.get(i) instanceof ListNote){
                title = ((ListNote) notes.get(i)).getName();
            }
            ((TextView) ((LinearLayout)cardView.getChildAt(0)).getChildAt(0)).setText(title);
            cardView.setVisibility(View.VISIBLE);
            final String noteId = notes.get(i).getId();
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TagNotesActivity.this, TextNote.class);
                    intent.putExtra(Constants.EXTRA_NOTEID,noteId);
                    startActivity(intent);
                }
            });
        }
    }


}
