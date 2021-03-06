package com.bitsplease.fridgynote.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.controller.ShoppingItems;

public class ShoppingItemAdaptor extends RecyclerView.Adapter<ShoppingItemAdaptor.ViewHolder> {
    private ShoppingItems shoppingItems;
    private String[] mKeys;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
        // each data item is just a string in this case


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShoppingItemAdaptor(ShoppingItems shoppingItems) {
        this.shoppingItems = shoppingItems;
        mKeys = shoppingItems.getKeyset().toArray(new String[shoppingItems.length()]);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShoppingItemAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        String value = shoppingItems.getShoppingItem(key).first.toString();

        Button deleteButton = holder.itemView.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                shoppingItems.deleteShoppingItem(key);
                notifyItemRemoved(position);
            }
        });

        item.setText(value);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return shoppingItems.length();
    }
}

