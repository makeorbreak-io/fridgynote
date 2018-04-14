package com.bitsplease.fridgynote.controller;

public class ListNoteItem {
    private String mText;
    private boolean mChecked;

    public ListNoteItem(String text) {
        this(text, false);
    }

    public ListNoteItem(String text, boolean checked) {
        mText = text;
        mChecked = checked;
    }

    public String getText() {
        return mText;
    }

    public boolean isChecked() {
        return mChecked;
    }
}
