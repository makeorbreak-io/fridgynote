package com.bitsplease.fridgynote.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
        super(obj);
        mName = obj.getString("title");
        //TODO parse do resto
    }

    public String getName() {
        return mName;
    }

    public List<ListNoteItem> getItems() {
        return mItems;
    }

    public boolean addItem(String item) {
        for(ListNoteItem i : mItems) {
            if(i.getText().equals(item)) {
                return false;
            }
        }
        if(!mItems.add(new ListNoteItem(item, false))) {
            return false;
        }
        // TODO update backend
        return true;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("_id", getId());
            obj.put("title", mName);
            JSONObject owner = new JSONObject();
            owner.put("userId", getOwner().first);
            owner.put("tagId", getOwner().second);
            obj.put("owner", owner);
            Iterator it = getSharedUsers().entrySet().iterator();
            while(it.hasNext()){
                JSONObject user = new JSONObject();
                Map.Entry pair = (Map.Entry)it.next();
                user.put("userId", pair.getKey());
                user.put("tagId", pair.getValue());
                obj.accumulate("shared", user);
            }

            for(ListNoteItem item : mItems) {
                JSONObject i = new JSONObject();
                i.put("body", item.getText());
                i.put("checked", item.isChecked());
                obj.accumulate("items", i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
