package com.bitsplease.fridgynote.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.bitsplease.fridgynote.activities.ListNoteActivity;
import com.bitsplease.fridgynote.activities.NewTagActivity;
import com.bitsplease.fridgynote.activities.TagNotesActivity;
import com.bitsplease.fridgynote.activities.TextNoteActivity;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;

import java.util.List;

public class TagHandler {
    private static final String TAG = "FN-TagHandler";

    public static boolean handleTag(final Context context, String tagId) {
        Log.d(TAG, "Handling tag " + tagId);
        if (tagId == null || tagId.isEmpty()) {
            return false;
        }

        if (BackendConnector.isTagKnown(context, tagId)) {
            Log.d(TAG, "Known");
            Reminders r = Reminders.getReminders();
            if(r.hasReminder(tagId)) {
                Log.d(TAG, "Reminder");
                Reminders.setReminder(context, r.getReminder(tagId));
                return true;
            }

            ShoppingItems s = ShoppingItems.getShoppingItems();
            if(s.hasShoppingItem(tagId)) {
                Log.d(TAG, "Shopping Item");
                final Pair<String,String> noteName = s.getShoppingItem(tagId);
                BackendConnector.getNoteTags(context, new BackEndCallback() {
                    @Override
                    public void tagNotesCallback(List<Note> response) {
                        for(Note n : response) {
                            Log.d(TAG, "checking " + noteName + " " +  n.getId());
                            if(n.getId().equals(noteName.second)) {
                                ListNote note = (ListNote) n;
                                note.addItem(noteName.first);
                                BackendConnector.updateListNote(context, note);
                            }
                        }
                    }
                });
                return true;
            }

            OwnedNoteTags t = OwnedNoteTags.getOwnedTags();
            if(t.hasOwnedTag(tagId)) {
                Log.d(TAG, "Owned tag");
                Intent intent = new Intent(context, TagNotesActivity.class);
                intent.putExtra("tag", tagId);
                context.startActivity(intent);
                return true;
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
