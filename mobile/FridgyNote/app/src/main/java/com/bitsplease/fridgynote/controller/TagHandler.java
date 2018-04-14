package com.bitsplease.fridgynote.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bitsplease.fridgynote.activities.NewTagActivity;
import com.bitsplease.fridgynote.activities.TextNoteActivity;
import com.bitsplease.fridgynote.utils.Constants;

public class TagHandler {

    public static void handleTag(Activity activity, String tagId) {
        if (tagId == null || tagId.isEmpty()) {
            return;
        }

        if (BackendConnector.isTagKnown(tagId)) {
            // TODO get tag type and launch activity/trigger
        } else {
            launchNewTagActivity(activity, tagId);
        }
    }

    public static void launchNewTagActivity(Activity activity, String tagId) {
        Intent intent = new Intent(activity, NewTagActivity.class);
        Bundle b = new Bundle();
        b.putString(Constants.EXTRA_TAGID, tagId);
        intent.putExtras(b);
        activity.startActivity(intent);
    }
}
