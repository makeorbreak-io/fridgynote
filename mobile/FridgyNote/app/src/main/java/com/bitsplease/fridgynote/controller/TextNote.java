package com.bitsplease.fridgynote.controller;

import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        super(textNote);

        // TODO trocar este codigo para parsar owner e shareds
        JSONObject obj = textNote.getJSONObject("owner");
        if(obj.getString("userId").equals(PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""))){
            setTagId(obj.getString("tagId"));
        }else{
            JSONArray sharedArray = textNote.getJSONArray("shared");
            for(int i = 0; i < sharedArray.length();i++){
                if(sharedArray.getJSONObject(i).getString("userId").equals(PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""))) {
                    setTagId(sharedArray.getJSONObject(i).getString("tagId"));
                }
            }
        }
        //super(textNote.getString("_id"));
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

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("_id", getId());
            obj.put("title", mTitle);
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

            for(String s : mImages) {
                JSONObject i = new JSONObject();
                i.put("path", s);
                obj.accumulate("images", i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
