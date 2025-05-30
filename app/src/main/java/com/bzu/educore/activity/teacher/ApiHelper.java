package com.bzu.educore.activity.teacher;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.model.user.TimeTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiHelper {

    private static final String TAG = "ApiHelper";

    private static ApiHelper instance;
    private RequestQueue requestQueue;
    private Context context;

    private ApiHelper(Context ctx) {
        context = ctx.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized ApiHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new ApiHelper(ctx);
        }
        return instance;
    }

    // Callback interface for async responses
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // Fetch teacher timetable without checking "success"
    public void getTeacherTimeTable(int teacherId, ApiCallback<List<TimeTable>> callback) {
        String url = "http://10.0.2.2/android/timetable.php?teacher_id=" + teacherId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<TimeTable> list = parseTimeTable(response);
                        callback.onSuccess(list);
                    } catch (Exception e) {
                        Log.e(TAG, "Parsing error", e);
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error", error);
                    String message = (error.getMessage() != null) ? error.getMessage() : error.toString();
                    callback.onError("Volley error: " + message);
                }
        );

        requestQueue.add(request);
    }

    // Parse timetable JSON assuming "subjects" and "classes" arrays, no "success" flag
    private List<TimeTable> parseTimeTable(JSONObject response) throws JSONException {
        List<TimeTable> list = new ArrayList<>();

        JSONArray subjectsArray = response.optJSONArray("subjects");
        JSONArray classesArray = response.optJSONArray("classes");

        // Basic sanity check
        if (subjectsArray == null || classesArray == null) {
            throw new JSONException("Missing 'subjects' or 'classes' in response");
        }

        // Map subjects by id for easy lookup
        // You may want to store more fields if needed
        // Here we just parse subjects and create TimeTable entries without day/start/end times (adjust as you like)

        for (int i = 0; i < subjectsArray.length(); i++) {
            JSONObject subj = subjectsArray.getJSONObject(i);

            TimeTable timeTable = new TimeTable();
            timeTable.setSubjectId(subj.optInt("id", -1));
            // You might want to fill other fields if your PHP returns them or adjust your data model

            list.add(timeTable);
        }

        // If you want to parse classes and combine with subjects, you'll need to decide how they relate to TimeTable objects
        // This example does not combine them but you can enhance it accordingly

        return list;
    }

    // Announce exam API with better error logging
    public void announceExam(JSONObject examData, ApiCallback<Void> callback) {
        String url = "http://10.0.2.2/android/exam.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, examData,
                response -> {
                    Log.i(TAG, "Exam announced successfully");
                    callback.onSuccess(null);
                },
                error -> {
                    Log.e(TAG, "Volley error", error);
                    String message = (error.getMessage() != null) ? error.getMessage() : error.toString();
                    callback.onError("Volley error: " + message);
                }
        );

        requestQueue.add(request);
    }
}
