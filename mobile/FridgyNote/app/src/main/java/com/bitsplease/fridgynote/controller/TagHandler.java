package com.bitsplease.fridgynote.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import com.bitsplease.fridgynote.activities.ListNoteActivity;
import com.bitsplease.fridgynote.activities.NewTagActivity;
import com.bitsplease.fridgynote.activities.TextNoteActivity;
import com.bitsplease.fridgynote.utils.Constants;

public class TagHandler {

    public static boolean handleTag(Context context, String tagId) {
        if (tagId == null || tagId.isEmpty()) {
            return false;
        }

        if (BackendConnector.isTagKnown(context, tagId)) {
            Reminders r = Reminders.getReminders();
            if(r.hasReminder(tagId)) {
                Reminders.setReminder(context, r.getReminder(tagId));
                return true;
            }

            ShoppingItems s = ShoppingItems.getShoppingItems();
            if(s.hasShoppingItem(tagId)) {
                Pair<String,String> noteName = s.getShoppingItem(tagId);
                ListNote note = BackendConnector.getListNote(noteName.second);
                if(note == null) {
                    Toast.makeText(context, "Unable to add shopping item.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return note.addItem(noteName.first);
            }
            // TODO get tag type and launch activity/trigger
        } else {
            launchNewTagActivity(context, tagId);
        }
        return false;
    }

    public static void launchNewTagActivity(Context context, String tagId) {
        Intent intent = new Intent(context, NewTagActivity.class);
        Bundle b = new Bundle();
        b.putString(Constants.EXTRA_TAGID, tagId);
        intent.putExtras(b);
        context.startActivity(intent);
    }
}
