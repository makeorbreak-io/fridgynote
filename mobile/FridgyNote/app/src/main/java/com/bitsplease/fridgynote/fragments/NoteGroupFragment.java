package com.bitsplease.fridgynote.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.BackendConnector;
import com.bitsplease.fridgynote.controller.NoteTag;

import java.util.List;



public class NoteGroupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notes_group_fragment_layout,
                container, false);

        List<NoteTag> tags = BackendConnector.getNoteTags(getActivity());
        String[] values = new String[tags.size()];
        for(int i =0; i < tags.size(); i++){
            values[i] = tags.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.note_group_row, R.id.label_note, values);
        ListView rv = view.findViewById(R.id.notes_groups);
        rv.setAdapter(adapter);
        return view;
    }


}
