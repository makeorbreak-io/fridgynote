package com.bitsplease.fridgynote.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bitsplease.fridgynote.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class BackendConnector {

    public static boolean isTagKnown(Context context, SharedPreferences prefs, String tagId) {
        Reminders r = Reminders.getReminders(prefs);
        if(r.hasReminder(tagId)) {
            return true;
        }
        return false;
    }

    public static TextNote getTextNote(String noteId) {
        List<String> images = new ArrayList<>();
        images.add("https://pbs.twimg.com/profile_images/949374088249671680/MuxDEZpD_400x400.jpg");
        images.add("https://upload.wikimedia.org/wikipedia/commons/0/0f/Eiffel_Tower_Vertical.JPG");
        images.add("https://ironcodestudio.com/wp-content/uploads/2015/03/css-remove-horizontal-scrollbar.jpg");
        return new TextNote(noteId, "Notinha", "Cenas cenas cenas", images);
    }

    public static ListNote getListNote(String noteId) {
        List<ListNoteItem> items = new ArrayList<>();
        items.add(new ListNoteItem("Bananas", false));
        items.add(new ListNoteItem("Chocapitos", false));
        items.add(new ListNoteItem("Tags NFC", true));
        items.add(new ListNoteItem("Memes", false));
        items.add(new ListNoteItem("BigMac", true));
        return new ListNote(noteId, "Comprinhas", items);
    }

    public static List<NoteTag> getNoteTags() {
        List<NoteTag> res = new ArrayList<>();
        res.add(new NoteTag("fridgynote1", "room"));
        res.add(new NoteTag("fridgynote2", "kitchen"));
        res.add(new NoteTag("fridgynote3", "office"));
        return res;
    }

    public static List<ListNote> getListNotes() {
        List<ListNote> res = new ArrayList<>();
        res.add(new ListNote("id", "Personal Shopping", new ArrayList<ListNoteItem>()));
        res.add(new ListNote("id", "Family Shopping", new ArrayList<ListNoteItem>()));
        return res;
    }

    public static Reminders getReminders(SharedPreferences preferences) {
        return Reminders.getReminders(preferences);
    }

    public static List<Note> getUnassignedNodes() {
        return new ArrayList<>();
    }

    public static boolean createNoteTag(String tagId, String name) {
        // TODO codar
        return false;
    }

    public static boolean createReminderTag(SharedPreferences prefs, String tagId, String name) {
        Reminders r = Reminders.getReminders(prefs);
        Log.d("FN-", "here1");
        if(r.hasReminder(tagId)) {
            Log.d("FN-", "here2");
            return false;
        }
        Log.d("FN-", "here3");
        r.addReminder(tagId, name);
        return true;
    }

    public static boolean createShoppingItemTag(String tagId, String name, ListNote listNote) {
        // TODO codar
        return false;
    }
}
