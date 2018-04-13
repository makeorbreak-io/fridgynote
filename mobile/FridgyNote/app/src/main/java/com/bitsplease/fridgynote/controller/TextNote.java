package com.bitsplease.fridgynote.controller;

import java.util.List;

public class TextNote extends Note {
    private String mTitle;
    private String mBody;
    private List<String> mImages;

    public TextNote(String id, String title, String body, List<String> images) {
        super(id);
        mTitle = title;
        mBody = body;
        mImages = images;
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
}
