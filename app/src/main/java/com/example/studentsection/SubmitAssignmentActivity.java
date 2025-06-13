package com.example.studentsection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;
import com.example.studentsection.model.AssignmentData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;
import android.webkit.MimeTypeMap;
import android.util.Base64;


public class SubmitAssignmentActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 100;
    private ListView assignmentList;
    private Button btnSelectFile, btnSubmit;
    private Uri selectedFileUri = null;
    private int selectedAssignmentId = -1;
    private RequestQueue queue;
    private String studentId = "20250001"; // Temporary until SharedPreferences is used

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        assignmentList = findViewById(R.id.assignmentList);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSubmit = findViewById(R.id.btnSubmit);

        queue = Volley.newRequestQueue(this);
        loadAssignments();

        assignmentList.setOnItemClickListener((parent, view, position, id) -> {
            AssignmentData assignment = (AssignmentData) parent.getItemAtPosition(position);
            selectedAssignmentId = assignment.getId();
            Toast.makeText(this, "Selected: " + assignment.getTitle(), Toast.LENGTH_SHORT).show();
        });

        btnSelectFile.setOnClickListener(v -> openFileChooser());

        btnSubmit.setOnClickListener(v -> {
            if (selectedAssignmentId == -1 || selectedFileUri == null) {
                Toast.makeText(this, "Select an assignment and a file.", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        });
    }

    private void loadAssignments() {
        // Fixed URL - removed the incorrect path structure
        String url = "http://10.0.2.2/edu-core/student/get_assignments.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Add logging to debug the response
                        Log.d("AssignmentResponse", "Response: " + response);

                        // Check if response contains error
                        if (response.contains("error")) {
                            Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<AssignmentData> list = new Gson().fromJson(response, new TypeToken<List<AssignmentData>>(){}.getType());
                        ArrayAdapter<AssignmentData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
                        assignmentList.setAdapter(adapter);

                    } catch (Exception e) {
                        Log.e("ParseError", "Error parsing JSON: " + e.getMessage());
                        Log.e("ParseError", "Response was: " + response);
                        Toast.makeText(this, "Error parsing assignments data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NetworkError", "Error loading assignments: " + error.getMessage());
                    Toast.makeText(this, "Error loading assignments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "image/*"});
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(this, "File selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close(); // Close the input stream

            String fileName = getFileName(selectedFileUri);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileName);

            String encodedFile = Base64.encodeToString(fileBytes, Base64.DEFAULT);

            String url = "http://10.0.2.2/edu-core/student/submit_assignment.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("UploadResponse", "Response: " + response);
                        Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show();
                        // Refresh the assignments list after successful upload
                        loadAssignments();
                    },
                    error -> {
                        Log.e("UploadError", "Upload error: " + error.getMessage());
                        Toast.makeText(this, "Upload failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("assignment_id", String.valueOf(selectedAssignmentId));
                    map.put("student_id", studentId);
                    map.put("file_name", fileName);
                    map.put("file_data", encodedFile);
                    return map;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            Toast.makeText(this, "File processing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FileError", "File processing error: " + e.toString());
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}