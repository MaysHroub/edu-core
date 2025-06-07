package com.bzu.educore.repository.registrar;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bzu.educore.util.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainRepository {

    private static MainRepository instance;
    private final Context context;

    private MainRepository(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        instance = new MainRepository(context);
    }

    public static synchronized MainRepository getInstance() {
        if (instance == null)
            throw new AssertionError("You have to call init first");
        return instance;
    }

    public void getAllItems(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getData(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public <T> void addItem(String url, T item, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        Gson gson = new Gson();
        String stdJson = gson.toJson(item);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(stdJson);
        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                listener,
                errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public <T> void updateItem(String url, T modifiedItem, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(modifiedItem);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonObject,
                listener,
                errorListener
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void deleteItemById(String url, Integer itemId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        url += "?id=" + itemId;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                listener,
                errorListener
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
