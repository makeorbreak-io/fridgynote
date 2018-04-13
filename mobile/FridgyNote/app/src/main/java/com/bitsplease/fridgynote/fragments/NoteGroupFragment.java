package com.bitsplease.fridgynote.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitsplease.fridgynote.R;

/**
 * Created by André on 13/04/2018.
 */

public class NoteGroupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_group_fragment_layout,
                container, false);
        return view;
    }


}
