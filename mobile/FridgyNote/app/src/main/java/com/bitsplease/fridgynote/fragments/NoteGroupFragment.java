package com.bitsplease.fridgynote.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.activities.LoginActivity;
import com.bitsplease.fridgynote.activities.TagNotesActivity;
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
import java.util.Set;


public class NoteGroupFragment extends Fragment implements BackEndCallback {

    Map<String, String> idMap = new HashMap<>();

    ListView rv;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.notes_group_fragment_layout,
                container, false);


        rv = view.findViewById(R.id.notes_groups);


        BackendConnector.getNoteTags(getActivity(), this);
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.notes_groups) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.tag_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                Iterator it = idMap.entrySet().iterator();
                TextView tv = rv.getChildAt(info.position).findViewById(R.id.label_note);
                String value = tv.getText().toString();

                SharedPreferences prefs = PreferenceUtils.getPrefs();

                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry) it.next();
                    Log.e("FN-DELETE", (String) pair.getValue());
                    if(value.equals(pair.getValue())){

                        SharedPreferences.Editor editor = prefs.edit();
                        String key = (String) pair.getKey();
                        idMap.remove(key);

                        editor.putString(Constants.KEY_OWNED_TAGS, toString(idMap));
                        Log.e("FN-DELETE2", prefs.getString(key, " asdasd"));
                        editor.apply();

                        break;
                    }
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public String toString(Map<String,String> idMap) {
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = idMap.keySet();
        for (String s : keySet) {
            builder.append(s);
            builder.append(";");
            builder.append(idMap.get(s));
            builder.append("#");
        }
        return builder.toString();
    }


    @Override
    public void tagNotesCallback(List<Note> notes) {
        List<String> notesLocation = new ArrayList<>();

        SharedPreferences prefs = PreferenceUtils.getPrefs();
        String s = prefs.getString(Constants.KEY_OWNED_TAGS, "");
        Log.e("FN-DEBUGTAGS", s);
        if(!s.equals("")){
            Map<String, String>  tags  = parseString(s);
            Iterator iterator = tags.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry pair = (Map.Entry) iterator.next();
                notesLocation.add(String.valueOf(pair.getValue()));
                idMap.put(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
            }
        }


        if(notes != null){
            for (Note n : notes){
                if(n.getTagId() == null || n.getTagId().equals("")){
                    notesLocation.add("Unassigned");
                }
            }
        }


        String[] values = notesLocation.toArray(new String[0]);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.note_group_row, R.id.label_note, values);

        rv.setAdapter(adapter);
        registerForContextMenu(rv);
        rv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v,
                                    int position, long arg3) {

                TextView tv = v.findViewById(R.id.label_note);

                String tagId = idMap.get(tv.getText());

                Intent intent = new Intent(getActivity(), TagNotesActivity.class);
                intent.putExtra("tag", tagId);
                startActivity(intent);

            }
        });


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
