package com.bitsplease.fridgynote.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.activities.TagNotesActivity;
import com.bitsplease.fridgynote.utils.BackEndCallback;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.ImageLoader;
import com.bitsplease.fridgynote.utils.ImageUploadCallback;
import com.bitsplease.fridgynote.utils.PreferenceUtils;
import com.bitsplease.fridgynote.utils.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BackendConnector {

    public static boolean isTagKnown(Context context, String tagId) {
        Reminders r = Reminders.getReminders();
        if (r.hasReminder(tagId)) {
            return true;
        }
        ShoppingItems s = ShoppingItems.getShoppingItems();
        if (s.hasShoppingItem(tagId)) {
            return true;
        }
        OwnedNoteTags t = OwnedNoteTags.getOwnedTags();
        if(t.hasOwnedTag(tagId)) {
            return true;
        }
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
                        callback.tagNotesCallback(parseAllNotes(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work!");
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, ""));
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    public static void uploadTextNote(Context context, final TextNote note) {
        Log.d("FN-test", "uploading note " + note.toJSON().toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/text/" + note.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.e("FN-ERROR", "Sending " + note.toJSON().toString());
                return note.toJSON().toString().getBytes();
            }
        };

        queue.add(stringRequest);
    }

    public static void createTextNote(Context context, final TextNote note) {
        Log.d("FN-test", "uploading note " + note.toJSON().toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/text/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.e("FN-ERROR", "Sending " + note.toJSON().toString());
                return note.toJSON().toString().getBytes();
            }
        };

        queue.add(stringRequest);
    }

    public static void uploadBitmap(Context context, final Bitmap bitmap, final ImageUploadCallback callback) {
        Log.d("FN-test", "uploading bitmap");
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/text/image";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.newImageId(response);
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
                Log.e("FN-ERROR", "That didn't work! " + error.getMessage());
                Log.e("FN-ERROR", "That didn't work! " + error.getLocalizedMessage());
                Log.e("FN-ERROR", "That didn't work! " + error.getCause());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/octet-stream");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return ImageLoader.convertBitmapToByteArrayUncompressed(bitmap);
            }
        };

        queue.add(stringRequest);
    }

    public static void updateListNote(Context context, final ListNote note) {
        Log.d("FN-test", note.toJSON().toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/list/" + note.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return note.toJSON().toString().getBytes();
            }
        };

        queue.add(stringRequest);
    }


    public static void createListNote(Context context, final ListNote note) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/list" ;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.e("FN-BODY", note.toJSON().toString());
                return note.toJSON().toString().getBytes();
            }
        };

        queue.add(stringRequest);
    }



    public static boolean createNoteTag(String tagId, String name) {
        OwnedNoteTags noteTag = OwnedNoteTags.getOwnedTags();
        if (noteTag.hasOwnedTag(tagId)) {
            return false;
        }
        noteTag.addOwnedTag(tagId, name);
        return true;
    }

    public static boolean createReminderTag(String tagId, String name) {
        Reminders r = Reminders.getReminders();
        if (r.hasReminder(tagId)) {
            return false;
        }
        r.addReminder(tagId, name);
        return true;
    }

    public static boolean createShoppingItemTag(String tagId, String name, ListNote listNote) {
        ShoppingItems r = ShoppingItems.getShoppingItems();
        if (r.hasShoppingItem(tagId)) {
            return false;
        }
        r.addShoppingItem(tagId, name, listNote.getId());
        return true;
    }

    public static List<Note> parseAllNotes(String response) {
        List<Note> result = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray textNotesArray = obj.getJSONArray("textNote");
            JSONArray listNotesArray = obj.getJSONArray("listNote");

            for (int index = 0; index < textNotesArray.length(); index++) {
                result.add(new TextNote(textNotesArray.getJSONObject(index)));
            }
            for (int i = 0; i < listNotesArray.length(); i++) {
                result.add(new ListNote(listNotesArray.getJSONObject(i)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Map<String, String> parseString(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();

        String[] tokens = str.split("#");
        for (String t : tokens) {
            if (t != null && !t.isEmpty()) {
                String[] keyValue = t.split(";");
                if (keyValue.length != 2) {
                    continue;
                }
                map.put(keyValue[0], keyValue[1]);
            }
        }

        return map;
    }

    public static void sendTextNoteToWebApp(Context context, final TextNote note) {
        Log.d("FN-test", "uploading web note " + note.toJSON().toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/send/" + note.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.e("FN-ERROR", "Sending web " + note.toJSON().toString());
                return note.toJSON().toString().getBytes();
            }
        };

        queue.add(stringRequest);
    }

    public static void deleteTextNote(Context context, String tagId){
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/text/" + tagId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", PreferenceUtils.getPrefs().getString(Constants.KEY_USERNAME, "moura"));
                headers.put("Content-Type", "application/json");
                return headers;
            }};

        queue.add(stringRequest);
    }


    public static void deleteListNote(Context context, String tagId) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "https://fridgynote.herokuapp.com/notes/list/" + tagId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FN- RESPONSE", "Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FN-ERROR", "That didn't work! " + url);
                Log.e("FN-ERROR", "That didn't work! " + error.toString());
            }
        });

        queue.add(stringRequest);
    }
}
