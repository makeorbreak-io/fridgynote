package com.bitsplease.fridgynote.controller;

/**
 * Created by Lago on 13/04/2018.
 */

public class Note {
    private String mId;

    public Note(String id) {
        mId = id;
    }

    public final String getId() {
        return mId;
    }
}
