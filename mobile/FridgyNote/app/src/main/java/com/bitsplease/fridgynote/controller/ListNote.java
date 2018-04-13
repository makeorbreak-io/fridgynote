package com.bitsplease.fridgynote.controller;

public class ListNote extends Note {
    private String mName;

    public ListNote(String id, String name) {
        super(id);
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
