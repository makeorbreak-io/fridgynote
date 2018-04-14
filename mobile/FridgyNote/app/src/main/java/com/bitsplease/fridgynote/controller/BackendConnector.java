package com.bitsplease.fridgynote.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitsplease.fridgynote.utils.BackEndCallback;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendConnector{

    public static boolean isTagKnown(String tagId) {
        return false;
    }


    public static void getNoteTags(Context context, final BackEndCallback callback) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://fridgynote.herokuapp.com/notes/me";
        JSONObject resp;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("FN- RESPONSE", "Response is: " + response);
                        callback.tagNotesCallback(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work!");
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", "moura");
                return headers;
            }
        };

        queue.add(stringRequest);

    }

    public static TextNote getTextNote(String noteId) {
        List<String> images = new ArrayList<>();
        images.add("https://pbs.twimg.com/profile_images/949374088249671680/MuxDEZpD_400x400.jpg");
        images.add("https://upload.wikimedia.org/wikipedia/commons/0/0f/Eiffel_Tower_Vertical.JPG");
        images.add("https://ironcodestudio.com/wp-content/uploads/2015/03/css-remove-horizontal-scrollbar.jpg");
        return new TextNote(noteId, "Notinha", "Cenas cenas cenas", images);
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
