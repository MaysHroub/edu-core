package com.bzu.educore.repository.registrar;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONArray;

public class SubjectRepository {

    private final Context context;

    public SubjectRepository(Context context) {
        this.context = context;
    }

    public void getAllSubjects(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, UrlManager.URL_GET_ALL_SUBJECTS, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
