package com.bitsplease.fridgynote.controller;

import android.content.SharedPreferences;
import android.util.Log;

import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TextNote extends Note {
    private String mTitle;
    private String mBody;
    private List<String> mImages;
    private List<String> mLabels;

    public TextNote(String id, String title, String body, List<String> images) {
        super(id);
        mTitle = title;
        mBody = body;
        mImages = images;
        mLabels = new ArrayList<>();
    }

    public TextNote(JSONObject textNote) throws JSONException {
        super(textNote);

        // TODO trocar este codigo para parsar owner e shareds
        /*
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
        */
        SharedPreferences prefs = PreferenceUtils.getPrefs();

        if(getOwner().first.equals(prefs.getString(Constants.KEY_USERNAME, ""))){
            setTagId(getOwner().second);
        }else{
            Map<String, String> shared = getSharedUsers();

            for(int i =0; i < shared.size(); i++){
                if(shared.containsKey(prefs.getString(Constants.KEY_USERNAME, ""))){
                    setTagId(shared.get(prefs.getString(Constants.KEY_USERNAME, "")));
                }
            }
        }

        //super(textNote.getString("_id"));
        mTitle = textNote.getString("title");
        mBody = textNote.getString("body");
        mImages = new ArrayList<>();
        mLabels = new ArrayList<>();
        JSONArray imagesList = textNote.getJSONArray("images");
        if(imagesList.length() > 0){
            for(int i = 0; i <imagesList.length(); i++){
                mImages.add(imagesList.getString(i));
            }
        }


        Log.e("FN- TEXTNOTE" , "title: " + mTitle + " body: " + mBody + " images: " + mImages.toString() + " id: " + getId() + " tagId: " + getTagId() + " mOwner: " + getOwner() + " shared: " + getSharedUsers().toString());
        /*
        JSONArray labelsList = textNote.getJSONArray("labels");
        for(int i = 0; i < labelsList.length(); i++){
            mLabels.add(labelsList.getString(i));
        }
        */
    }

    public void setTitle(String title) {
        mTitle = title;
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
            obj.put("tagId", getId());
            obj.put("title", mTitle);
            JSONObject owner = new JSONObject();
            owner.put("userId", getOwner().first);
            owner.put("tagId", getOwner().second);
            obj.put("owner", owner);
            obj.put("body", mBody);
            Iterator it = getSharedUsers().entrySet().iterator();
            if(!it.hasNext()){
                JSONArray array = new JSONArray();
                obj.put("shared", array);
            }
            while(it.hasNext()){
                JSONObject user = new JSONObject();
                Map.Entry pair = (Map.Entry)it.next();
                user.put("userId", pair.getKey());
                user.put("tagId", pair.getValue());
                obj.accumulate("shared", user);
            }

            if(mImages.size() == 0){
                obj.put("images", new JSONArray());
            }
            for(String s : mImages) {
                obj.accumulate("images", s);
            }
            if(mLabels.size() == 0){
                obj.put("labels", new JSONArray());
            }
            for(String s : mLabels) {
                obj.accumulate("labels", s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public List<String> getLabels() {
        return mLabels;
    }

    public void setLabels(List<String> labels) {
        this.mLabels = labels;
    }
}
