package com.bitsplease.fridgynote.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TextNote extends Note {
    private String mTitle;
    private String mBody;
    private List<String> mImages;

    public TextNote(String id, String title, String body, List<String> images) {
        super(id);
        mTitle = title;
        mBody = body;
        mImages = images;
    }

    public TextNote(JSONObject textNote) throws JSONException {
        super(textNote.getString("_id"));
        mTitle = textNote.getString("title");
        mBody = textNote.getString("body");
        JSONArray imagesList = textNote.getJSONArray("images");
        for(int i = 0; i <imagesList.length(); i++){
            mImages.add(imagesList.getString(i));
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public List<String> getImages() {
        return mImages;
    }
}
