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

            List<String> notesLocation = new ArrayList<>();
            JSONArray textNotesArray = obj.getJSONArray("textNote");
            JSONArray listNotesArray = obj.getJSONArray("listNote");


            String noteTags = PreferenceUtils.getPrefs().getString(Constants.KEY_OWNED_TAGS, "");
            Log.e("FN-OWNED" , noteTags);
            if(noteTags != ""){
                Map<String, String> ownedTags = parseString(noteTags);
                Iterator it = ownedTags.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    Log.e("FN-OwnedTags", pair.getKey() + " = " + pair.getValue());
                    if(!notesLocation.contains(pair.getValue())){
                        notesLocation.add((String) pair.getValue());
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }


           // Log.e("FN-jsonarray", textNotesArray.getJSONObject(0).getString("title"));
           // Log.e("FN-jsonarray1", listNotesArray.getJSONObject(0).getString("title"));






            int index;

            for(index=0; index< textNotesArray.length(); index++){

                textNotes.add(new TextNote(textNotesArray.getJSONObject(index)));
                if( !notesLocation.contains(textNotes.get(index).getTitle())){
                    notesLocation.add(textNotes.get(index).getTitle());
                }
            }
            for(int i= 0; i < listNotesArray.length(); i++){
                listNotes.add(new ListNote(listNotesArray.getJSONObject(i)));
                if( !notesLocation.contains(listNotes.get(i).getName())){
                    notesLocation.add(listNotes.get(i).getName());
                }
            }

            String[] values = notesLocation.toArray(new String[0]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.note_group_row, R.id.label_note, values);
            ListView rv = view.findViewById(R.id.notes_groups);
            rv.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


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
