package com.bitsplease.fridgynote.controller;

public class NoteTag extends Tag {
    private String mName;

    public NoteTag(String id, String name) {
        super(id);
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
