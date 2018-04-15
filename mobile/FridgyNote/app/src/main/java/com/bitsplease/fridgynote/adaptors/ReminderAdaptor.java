package com.bitsplease.fridgynote.adaptors;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.Reminders;
import com.bitsplease.fridgynote.controller.ShoppingItems;

public class ReminderAdaptor extends RecyclerView.Adapter<ReminderAdaptor.ViewHolder> {
    private Reminders reminders;
    private String[] mKeys;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private String tag;
        public ViewHolder(View itemView) {
            super(itemView);
        }
        // each data item is just a string in this case


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReminderAdaptor(Reminders reminders) {
        this.reminders = reminders;
        mKeys = reminders.getKeyset().toArray(new String[reminders.length()]);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReminderAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item_adaptor, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView item = holder.itemView.findViewById(R.id.item);

        final String key = mKeys[position];
        final String value = reminders.getReminder(key);
        item.setText(value);

        Button deleteButton = holder.itemView.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                reminders.deleteReminder(key);
                notifyItemRemoved(position);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reminders.length();
    }
}

