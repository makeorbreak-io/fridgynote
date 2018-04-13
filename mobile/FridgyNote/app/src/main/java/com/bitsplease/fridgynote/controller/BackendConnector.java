package com.bitsplease.fridgynote.controller;

import java.util.ArrayList;
import java.util.List;

public class BackendConnector {

    public static boolean isTagKnown(String tagId) {
        return false;
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
        res.add(new ListNote("Personal Shopping"));
        res.add(new ListNote("Family Shopping"));
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
