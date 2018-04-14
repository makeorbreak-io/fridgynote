package com.bitsplease.fridgynote.controller;

import java.util.ArrayList;
import java.util.List;

public class BackendConnector {

    public static boolean isTagKnown(String tagId) {
        return false;
    }

    public static TextNote getTextNote(String noteId) {
        List<String> images = new ArrayList<>();
        images.add("https://pbs.twimg.com/profile_images/949374088249671680/MuxDEZpD_400x400.jpg");
        images.add("https://upload.wikimedia.org/wikipedia/commons/0/0f/Eiffel_Tower_Vertical.JPG");
        images.add("https://ironcodestudio.com/wp-content/uploads/2015/03/css-remove-horizontal-scrollbar.jpg");
        return new TextNote(noteId, "Notinha", "Cenas cenas cenas", images);
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
        res.add(new ListNote("id", "Personal Shopping"));
        res.add(new ListNote("id", "Family Shopping"));
        return res;
    }

    public static List<Note> getUnassignedNodes() {
        return new ArrayList<>();
    }

    public static boolean createNoteTag(String tagId, String name) {
        // TODO codar
        return false;
    }

    public static boolean createReminderTag(String tagId, String name) {
        // TODO codar
        return false;
    }

    public static boolean createShoppingItemTag(String tagId, String name, ListNote listNote) {
        // TODO codar
        return false;
    }
}
