package com.bitsplease.fridgynote.controller;

public class ListNote extends Note {
    private String mName;

    public ListNote(String name) {
        super();
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
