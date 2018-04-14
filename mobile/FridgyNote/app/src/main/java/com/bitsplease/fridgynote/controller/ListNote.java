package com.bitsplease.fridgynote.controller;

import android.content.SharedPreferences;

import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.util.ArrayList;
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

    public ListNote(JSONObject listNote) throws JSONException {
        super(listNote);
        mName = listNote.getString("title");
        //TODO parse do resto
/*
        JSONObject obj = listNote.getJSONObject("owner");
        if(obj.getString("userId").equals(PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""))){
            setTagId(obj.getString("tagId"));
        }else{
            JSONArray sharedArray = listNote.getJSONArray("shared");
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

        List<ListNoteItem> items = new ArrayList<>();

        JSONArray itemsArray = listNote.getJSONArray("items");
        for(int i =0; i< itemsArray.length(); i++){
            JSONObject item = itemsArray.getJSONObject(i);
            items.add(new ListNoteItem(item.getString("body"), item.getBoolean("checked")));
        }
        this.mItems = items;

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
