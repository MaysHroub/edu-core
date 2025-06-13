package com.bzu.educore.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class FileUploadUtil {

    public interface UploadCallback {
        void onSuccess(String fileUrl);
        void onError(String error);
    }

    public static void uploadFile(Context context, Uri fileUri, UploadCallback callback) {
        try {
            // Get file name
            String fileName = getFileName(context, fileUri);
            if (fileName == null) {
                callback.onError("Could not get file name");
                return;
            }

            // Read file content
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                callback.onError("Could not read file");
                return;
            }

            byte[] fileContent = getBytes(inputStream);
            inputStream.close();

            // Create multipart request
            MultipartRequest multipartRequest = new MultipartRequest(
                    Request.Method.POST,
                    UrlManager.URL_UPLOAD_FILE, // You need to add this to your UrlManager
                    fileName,
                    fileContent,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                callback.onSuccess(jsonResponse.getString("file_url"));
                            } else {
                                callback.onError(jsonResponse.optString("error", "Upload failed"));
                            }
                        } catch (JSONException e) {
                            callback.onError("Invalid response format");
                        }
                    },
                    error -> callback.onError(error.getMessage() != null ? error.getMessage() : "Upload failed")
            );

            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);

        } catch (Exception e) {
            callback.onError("Error preparing file: " + e.getMessage());
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static class MultipartRequest extends Request<String> {
        private final String fileName;
        private final byte[] fileContent;
        private final Response.Listener<String> listener;

        public MultipartRequest(int method, String url, String fileName, byte[] fileContent,
                                Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.fileName = fileName;
            this.fileContent = fileContent;
            this.listener = listener;
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data; boundary=" + getBoundary();
        }

        @Override
        public byte[] getBody() {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            try {
                dataOutputStream.writeBytes("--" + getBoundary() + "\r\n");
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
                dataOutputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                dataOutputStream.write(fileContent);
                dataOutputStream.writeBytes("\r\n--" + getBoundary() + "--\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outputStream.toByteArray();
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            String responseString;
            try {
                responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                responseString = new String(response.data);
            }
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(String response) {
            listener.onResponse(response);
        }

        private String getBoundary() {
            return "apiclient-" + System.currentTimeMillis();
        }
    }
}