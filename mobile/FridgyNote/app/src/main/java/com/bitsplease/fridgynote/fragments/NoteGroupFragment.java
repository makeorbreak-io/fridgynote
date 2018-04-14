package com.bitsplease.fridgynote.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.activities.LoginActivity;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.ListNote;
import com.bitsplease.fridgynote.controller.Note;
import com.bitsplease.fridgynote.controller.NoteTag;
import com.bitsplease.fridgynote.controller.OwnedNoteTags;
import com.bitsplease.fridgynote.controller.TextNote;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NoteGroupFragment extends Fragment implements BackEndCallback {

    List<TextNote> textNotes = new ArrayList<>();
    List<ListNote> listNotes = new ArrayList<>();


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
    public void tagNotesCallback(List<Note> notes) {
        List<String> notesLocation = new ArrayList<>();
        notesLocation.add("Tostas tens de corrigir isto");
        notesLocation.add("Não te esqueças");

        String[] values = notesLocation.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.note_group_row, R.id.label_note, values);
        ListView rv = view.findViewById(R.id.notes_groups);
        rv.setAdapter(adapter);
    }

    private Map<String, String> parseString(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();

        String[] tokens = str.split("#");
        for (String t : tokens) {
            if (t != null && !t.isEmpty()) {
                String[] keyValue = t.split(";");
                if (keyValue.length != 2) {
                    continue;
                }
                map.put(keyValue[0], keyValue[1]);
            }
        }

        return map;
    }
}
