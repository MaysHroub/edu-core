<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');
require_once 'connection.php';

try {
    // Check if file was uploaded
    if (!isset($_FILES['file']) || $_FILES['file']['error'] !== UPLOAD_ERR_OK) {
        http_response_code(400);
        echo json_encode(["error" => "No file uploaded or upload error"]);
        exit();
    }

    $file = $_FILES['file'];
    
    // Validate file size (max 10MB)
    $maxSize = 10 * 1024 * 1024; // 10MB in bytes
    if ($file['size'] > $maxSize) {
        http_response_code(400);
        echo json_encode(["error" => "File too large. Maximum size is 10MB"]);
        exit();
    }

    // Allowed file types
    $allowedTypes = ['pdf', 'doc', 'docx', 'txt', 'jpg', 'jpeg', 'png'];
    $fileExtension = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    
    if (!in_array($fileExtension, $allowedTypes)) {
        http_response_code(400);
        echo json_encode(["error" => "File type not allowed. Allowed types: " . implode(', ', $allowedTypes)]);
        exit();
    }

    // Get assignment ID from POST data (will be set after assignment is created)
    $assignmentId = isset($_POST['assignment_id']) ? $_POST['assignment_id'] : 'temp';
    
    // Create uploads directory if it doesn't exist
    $uploadDir = 'uploads/assignments/' . $assignmentId . '/';
    if (!file_exists($uploadDir)) {
        mkdir($uploadDir, 0777, true);
    }

    // Generate unique filename
    $uniqueFileName = time() . '_' . uniqid() . '.' . $fileExtension;
    $uploadPath = $uploadDir . $uniqueFileName;

    // Move uploaded file
    if (move_uploaded_file($file['tmp_name'], $uploadPath)) {
        // Return the file URL that can be stored in database
        $fileUrl = 'uploads/assignments/' . $assignmentId . '/' . $uniqueFileName;
        
        echo json_encode([
            "success" => true,
            "file_url" => $fileUrl,
            "original_name" => $file['name'],
            "message" => "File uploaded successfully"
        ]);
    } else {
        http_response_code(500);
        echo json_encode(["error" => "Failed to move uploaded file"]);
    }

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Server error: " . $e->getMessage()]);
}
?>