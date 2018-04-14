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
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagNotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_notes);
        TextNote test = new TextNote("123","dada","xrdcfvgbshd",new ArrayList<String>());
        createTextNoteList(Arrays.asList(test));
    }

    public void createTextNoteList(List<TextNote> notes){
        for(int i = 0; i< notes.size();i++){
            int id = getResources().getIdentifier("card" + (i + 1), "id", this.getPackageName());
            CardView cardView= findViewById(id);
            ((TextView) ((LinearLayout)cardView.getChildAt(0)).getChildAt(0)).setText(notes.get(i).getTitle());
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

    public void createListNoteList(List<ListNote> notes){
        for(int i = 0; i< notes.size();i++){
            int id = getResources().getIdentifier("card" + (i + 1), "id", this.getPackageName());
            CardView cardView= findViewById(id);
            ((TextView) ((LinearLayout)cardView.getChildAt(0)).getChildAt(0)).setText(notes.get(i).getName());
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
