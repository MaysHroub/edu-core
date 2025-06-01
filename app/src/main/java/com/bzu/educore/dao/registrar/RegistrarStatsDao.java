package com.bzu.educore.dao.registrar;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONArray;

public class RegistrarStatsDao {

    private final Context context;

    public RegistrarStatsDao(Context context) {
        this.context = context;
    }

    public void getSectionCount(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.GET, UrlManager.URL_GET_SUBJECT_COUNT, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getStudentCount(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.GET, UrlManager.URL_GET_STUDENT_COUNT, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getTeacherCount(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.GET, UrlManager.URL_GET_TEACHER_COUNT, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getTeachersPerSubject(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                UrlManager.URL_GET_TEACHER_PER_SUBJECT,
                null, // no request body
                listener,
                errorListener
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getStudentsPerGrade(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                UrlManager.URL_GET_STUDENT_PER_GRADE,
                null, // no request body
                listener,
                errorListener
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
