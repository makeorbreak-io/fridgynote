package com.bitsplease.fridgynote.controller;

import android.app.Activity;

public class TagHandler {

    public static void handleTag(Activity activity, String tagId) {
        if(BackendConnector.isTagKnown(tagId)) {
            // TODO get tag type and launch activity/trigger
        } else {
            
        }
    }
}
