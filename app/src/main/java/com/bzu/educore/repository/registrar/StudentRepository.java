package com.bzu.educore.repository.registrar;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bzu.educore.activity.registrar.ui.student_management.DummyStudent;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentRepository {
    private final Context context;

    public StudentRepository(Context context) {
        this.context = context;
    }

    public void addStudent(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, DummyStudent student) {
        Gson gson = new Gson();
        String stdJson = gson.toJson(student);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(stdJson);
        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                UrlManager.URL_ADD_NEW_STUDENT,
                jsonObject,
                listener,
                errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getStudentCountForCurrentYear(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.GET, UrlManager.URL_GET_STUDENT_COUNT_FOR_CURRENT_YEAR, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getAllClassrooms(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, UrlManager.URL_GET_ALL_CLASSROOMS, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
