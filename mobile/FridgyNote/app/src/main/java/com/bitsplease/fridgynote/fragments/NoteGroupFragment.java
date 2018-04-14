package com.bitsplease.fridgynote.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.NoteTag;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.BackEndCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class NoteGroupFragment extends Fragment implements BackEndCallback {

    List<TextNote> textNotes = new ArrayList<>();
    List<ListNote>  listNotes = new ArrayList<>();
    

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.notes_group_fragment_layout,
                container, false);


        BackendConnector.getNoteTags(getActivity(), this);
        return view;

    }


    @Override
    public void tagNotesCallback(String response) {
        JSONObject obj;
        try {
            obj = new JSONObject(response);

            JSONArray textNotesArray = obj.getJSONArray("textNote");
            JSONArray listNotesArray = obj.getJSONArray("listNote");
            Log.e("FN-jsonarray", textNotesArray.getJSONObject(0).getString("title"));
            Log.e("FN-jsonarray1", listNotesArray.getJSONObject(0).getString("title"));

            String[] values = new String[textNotesArray.length() + listNotesArray.length()];
            int index;

            for(index=0; index< textNotesArray.length(); index++){
                textNotes.add(new TextNote(textNotesArray.getJSONObject(index)));
                values[index] = textNotes.get(index).getTitle();
            }
            for(int i= 0; i < listNotesArray.length(); i++){
                listNotes.add(new ListNote(listNotesArray.getJSONObject(i)));
                values[i+index] = listNotes.get(i).getName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.note_group_row, R.id.label_note, values);
            ListView rv = view.findViewById(R.id.notes_groups);
            rv.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
