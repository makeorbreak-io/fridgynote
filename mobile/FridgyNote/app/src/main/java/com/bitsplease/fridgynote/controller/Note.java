package com.bitsplease.fridgynote.controller;

import java.util.ArrayList;
import java.util.List;

public class Note {
    private String mId;
    private String mTagId;
    private String mOwner;
    private List<String> mShared;

    public Note(String id) {
        mId = id;
    }

    public final String getId() {
        return mId;
    }

    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public void setSharedUsers(List<String> users) {
        mShared = users;
    }

    public String getTagId() {
        return mTagId;
    }

    public String getOwner() {
        return mOwner;
    }

    public List<String> getSharedUsers() {
        return mShared;
    }

    public List<String> getAllUsers() {
        List<String> res = new ArrayList<>(mShared);
        res.add(getOwner());
        return res;
    }

    public List<String> getAllUsersExcept(String user) {
        List<String> res = new ArrayList<>(mShared);
        res.add(getOwner());
        res.remove(user);
        return res;
    }
}
