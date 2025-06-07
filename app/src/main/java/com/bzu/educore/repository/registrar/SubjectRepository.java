package com.bzu.educore.repository.registrar;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubjectRepository {

    private final Context context;

    public SubjectRepository(Context context) {
        this.context = context;
    }

    public void getAllSubjects(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, UrlManager.URL_GET_ALL_SUBJECTS, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void updateSubject(Subject modifiedSubject, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(modifiedSubject);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                UrlManager.URL_UPDATE_SUBJECT,
                jsonObject,
                listener,
                errorListener
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
