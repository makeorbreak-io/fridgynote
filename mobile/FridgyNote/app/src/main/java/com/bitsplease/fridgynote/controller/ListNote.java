package com.bitsplease.fridgynote.controller;

import java.util.List;

public class ListNote extends Note {
    private String mName;
    private List<ListNoteItem> mItems;

    public ListNote(String id, String name, List<ListNoteItem> items) {
        super(id);
        mName = name;
        mItems = items;
    }

    public String getName() {
        return mName;
    }

    public List<ListNoteItem> getItems() {
        return mItems;
    }
}
