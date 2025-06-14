package com.bzu.educore.activity.student.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bzu.educore.R;
import com.bzu.educore.util.VolleySingleton;
import com.example.studentsection.model.AssignmentData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitAssignmentFragment extends Fragment {

    private static final int FILE_SELECT_CODE = 100;
    private ListView assignmentList;
    private Button btnSelectFile, btnSubmit;
    private Uri selectedFileUri = null;
    private int selectedAssignmentId = -1;
    private String studentId = "S001";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_submit_assignment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assignmentList = rootView.findViewById(R.id.assignmentList);
        btnSelectFile = rootView.findViewById(R.id.btnSelectFile);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);

        loadAssignments();

        assignmentList.setOnItemClickListener((parent, v, position, id) -> {
            AssignmentData assignment = (AssignmentData) parent.getItemAtPosition(position);
            selectedAssignmentId = assignment.getId();
            Toast.makeText(requireContext(), "Selected: " + assignment.getTitle(), Toast.LENGTH_SHORT).show();
        });

        btnSelectFile.setOnClickListener(v -> openFileChooser());
        btnSubmit.setOnClickListener(v -> {
            if (selectedAssignmentId == -1 || selectedFileUri == null) {
                Toast.makeText(requireContext(), "Select an assignment and a file.", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        });
    }

    private void loadAssignments() {
        String url = "http://10.0.2.2/edu-core/get_assignments.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("AssignmentResponse", "Response: " + response);

                        if (response.contains("error")) {
                            Toast.makeText(requireContext(), "Error: " + response, Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<AssignmentData> list = new Gson().fromJson(response, new TypeToken<List<AssignmentData>>() {}.getType());
                        ArrayAdapter<AssignmentData> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_list_item_1, list);
                        assignmentList.setAdapter(adapter);

                    } catch (Exception e) {
                        Log.e("ParseError", "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error parsing assignments data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Upload failed";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    } else if (error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UploadError", "Volley error: " + errorMessage);
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "image/*"});
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(requireContext(), "File selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedFileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            String fileName = getFileName(selectedFileUri);
            String encodedFile = Base64.encodeToString(fileBytes, Base64.DEFAULT);

            String url = "http://10.0.2.2/edu-core/student/submit_assignment.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("UploadResponse", "Response: " + response);
                        Toast.makeText(requireContext(), "File uploaded successfully!", Toast.LENGTH_SHORT).show();
                        loadAssignments();
                    },
                    error -> {
                        Log.e("UploadError", "Upload error: " + error.getMessage());
                        Toast.makeText(requireContext(), "Upload failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);

        } catch (Exception e) {
            Toast.makeText(requireContext(), "File processing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FileError", "File processing error: " + e.toString());
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
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
