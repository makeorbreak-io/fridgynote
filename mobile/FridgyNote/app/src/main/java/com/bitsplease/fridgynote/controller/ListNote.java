package com.bitsplease.fridgynote.controller;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class ListNote extends Note {
    private String mName;
    private List<ListNoteItem> mItems;

    public ListNote(String id, String name, List<ListNoteItem> items) {
        super(id);
        mName = name;
        mItems = items;
    }

    public ListNote(JSONObject obj) throws JSONException {
        super(obj.getString("_id"));
        mName = obj.getString("title");
    }

    public String getName() {
        return mName;
    }

    public List<ListNoteItem> getItems() {
        return mItems;
    }
}
