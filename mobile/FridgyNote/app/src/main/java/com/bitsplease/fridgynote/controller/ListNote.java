package com.bitsplease.fridgynote.controller;

import org.json.JSONException;
import org.json.JSONObject;

public class ListNote extends Note {
    private String mName;

    public ListNote(String id, String name) {
        super(id);
        mName = name;
    }

    public ListNote(JSONObject obj) throws JSONException {
        super(obj.getString("_id"));
        mName = obj.getString("title");
    }

    public String getName() {
        return mName;
    }
}
