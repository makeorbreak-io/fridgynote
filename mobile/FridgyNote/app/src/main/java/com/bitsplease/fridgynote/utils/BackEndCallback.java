package com.bitsplease.fridgynote.utils;

import android.content.SharedPreferences;

import com.bitsplease.fridgynote.controller.Note;

import java.util.List;

public interface BackEndCallback {
    public void tagNotesCallback(List<Note> response);
}
