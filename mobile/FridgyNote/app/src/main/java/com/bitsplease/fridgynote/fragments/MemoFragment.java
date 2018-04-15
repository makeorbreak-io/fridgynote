package com.bitsplease.fridgynote.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.adaptors.ReminderAdaptor;
import com.bitsplease.fridgynote.adaptors.ShoppingItemAdaptor;
import com.bitsplease.fridgynote.controller.Reminders;
import com.bitsplease.fridgynote.controller.ShoppingItems;

/**
 * Created by André on 13/04/2018.
 */

public class MemoFragment extends Fragment {
    private Reminders reminders= new Reminders();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memo_fragment_layout,
                container, false);
        reminders.addReminder("Teste","Lembrete");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_reminder_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ReminderAdaptor(reminders);
        mRecyclerView.setAdapter(mAdapter);

        return view;

    }


}
