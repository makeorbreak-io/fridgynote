package com.bitsplease.fridgynote.controller;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Note {
    private String mId;
    private String mTagId;
    private Pair<String, String> mOwner;
    private Map<String,String> mShared;
    private List<String> mLabels;

    public Note(String id) {
        mId = id;
    }

    public Note(JSONObject obj) throws JSONException {
       mId = obj.getString("_id");
       // TODO parse owner e shared
    }

    public final String getId() {
        return mId;
    }

    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    public void setOwner(String owner, String tagId) {
        mOwner = new Pair<>(owner, tagId);
    }

    public void setSharedUsers(Map<String, String> sharedUsers) {
        mShared = sharedUsers;
    }

    public String getTagId() {
        return mTagId;
    }

    public Pair<String, String> getOwner() {
        return mOwner;
    }

    public Map<String, String> getSharedUsers() {
        return mShared;
    }

    public List<String> getLabels() {
        return mLabels;
    }

    public Map<String, String> getAllUsers() {
        Map<String, String> res = new HashMap<>(mShared);
        res.put(getOwner().first, getOwner().second);
        return res;
    }

    public Map<String, String> getAllUsersExcept(String user) {
        Map<String, String> res = new HashMap<>(mShared);
        res.put(getOwner().first, getOwner().second);
        res.remove(user);
        return res;
    }

    public boolean removeUser(String user){
        if(mShared.containsKey(user)){
            mShared.remove(user);
            return true;
        }else{
            Log.e("FN-REMOVE", "no user with that name");
            return false;
        }
    }

    public abstract JSONObject toJSON();
}
