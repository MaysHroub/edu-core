package com.bzu.educore.repository.registrar;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.activity.registrar.ui.teacher_registration.DummyTeacher;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherRepository {

    private final Context context;

    public TeacherRepository(Context context) {
        this.context = context;
    }

    public void addTeacher(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, DummyTeacher teacher) {
        Gson gson = new Gson();
        String tchrJson = gson.toJson(teacher);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(tchrJson);
        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                UrlManager.URL_ADD_NEW_TEACHER,
                jsonObject,
                listener,
                errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getAllSubjects(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, UrlManager.URL_GET_ALL_SUBJECTS, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
