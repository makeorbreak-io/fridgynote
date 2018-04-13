package com.bitsplease.fridgynote.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitsplease.fridgynote.R;

/**
 * Created by André on 13/04/2018.
 */

public class MemoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memo_fragment_layout,
                container, false);
        return view;
    }


}
