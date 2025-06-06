<?php
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Only POST method allowed']);
    exit;
}

try {
    // Validate required fields
    $required_fields = ['assignment_id', 'student_id', 'file_name', 'file_data'];
    $missing_fields = [];
    
    foreach ($required_fields as $field) {
        if (!isset($_POST[$field]) || empty($_POST[$field])) {
            $missing_fields[] = $field;
        }
    }
    
    if (!empty($missing_fields)) {
        throw new Exception("Missing required fields: " . implode(', ', $missing_fields));
    }
    
    // Extract and validate data
    $assignment_id = filter_var($_POST['assignment_id'], FILTER_VALIDATE_INT);
    $student_id = filter_var($_POST['student_id'], FILTER_SANITIZE_STRING);
    $file_name = filter_var($_POST['file_name'], FILTER_SANITIZE_STRING);
    $file_data = base64_decode($_POST['file_data']);
    
    if (!$assignment_id || !$file_data) {
        throw new Exception("Invalid assignment_id or file_data");
    }
    
    // Verify assignment exists and belongs to student's class
    $stmt = $conn->prepare("
        SELECT a.deadline, t.class_id
        FROM Assignment a
        JOIN Task t ON a.id = t.id
        JOIN Student s ON t.class_id = s.class_id
        WHERE a.id = ? AND s.id = ?
    ");
    
    $stmt->bind_param("is", $assignment_id, $student_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        throw new Exception("Invalid assignment or student");
    }
    
    $assignment = $result->fetch_assoc();
    
    // Check if submission is past deadline
    if (strtotime($assignment['deadline']) < time()) {
        throw new Exception("Submission deadline has passed");
    }
    
    // Set up upload directory
    $upload_dir = "../uploads/assignments/";
    if (!is_dir($upload_dir)) {
        if (!mkdir($upload_dir, 0777, true)) {
            throw new Exception("Failed to create upload directory");
        }
    }
    
    // Generate unique filename
    $file_extension = pathinfo($file_name, PATHINFO_EXTENSION);
    $safe_filename = $assignment_id . '_' . $student_id . '_' . uniqid() . '.' . $file_extension;
    $target_file = $upload_dir . $safe_filename;
    
    // Save file
    if (!file_put_contents($target_file, $file_data)) {
        throw new Exception("Failed to save file");
    }
    
    // Update database
    $relative_url = "uploads/assignments/" . $safe_filename;
    $current_date = date("Y-m-d");
    
    $stmt = $conn->prepare("
        INSERT INTO AssignmentSubmission (id, student_id, submission_file_url, submission_date)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE 
            submission_file_url = VALUES(submission_file_url),
            submission_date = VALUES(submission_date)
    ");
    
    $stmt->bind_param("isss", $assignment_id, $student_id, $relative_url, $current_date);
    
    if (!$stmt->execute()) {
        // If database update fails, delete the uploaded file
        unlink($target_file);
        throw new Exception("Failed to record submission");
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Assignment submitted successfully',
        'file_url' => $relative_url
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'error' => $e->getMessage()
    ]);
} finally {
    if (isset($stmt)) {
        $stmt->close();
    }
    if (isset($conn)) {
        $conn->close();
    }
}
?>