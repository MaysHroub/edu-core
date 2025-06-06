<?php
header('Content-Type: application/json');

// Include DB connection
require_once 'db_connect.php';

// Read the incoming JSON payload
$input = json_decode(file_get_contents("php://input"), true);

// Check for required data - the Android app sends "marks" array
if (!isset($input['marks']) || !is_array($input['marks'])) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Invalid or incomplete input. Expected 'marks' array."]);
    exit();
}

$marks = $input['marks']; // array of { task_id, student_id, mark }

// Prepare statement for REPLACE INTO TaskResult
$stmt = $conn->prepare("REPLACE INTO TaskResult (task_id, student_id, mark, feedback) VALUES (?, ?, ?, ?)");

if (!$stmt) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Failed to prepare statement: " . $conn->error]);
    exit();
}

$success_count = 0;
$error_count = 0;

// Process each mark entry
foreach ($marks as $mark_entry) {
    // Validate required fields
    if (!isset($mark_entry['task_id']) || !isset($mark_entry['student_id'])) {
        $error_count++;
        continue;
    }

    $task_id = $mark_entry['task_id'];
    $student_id = $mark_entry['student_id'];
    $mark = isset($mark_entry['mark']) && $mark_entry['mark'] !== null ? $mark_entry['mark'] : null;
    $feedback = isset($mark_entry['feedback']) ? $mark_entry['feedback'] : null;

    // Bind parameters (i = integer, s = string, d = double)
    $stmt->bind_param("isds", $task_id, $student_id, $mark, $feedback);
    
    if ($stmt->execute()) {
        $success_count++;
    } else {
        $error_count++;
        error_log("Failed to save mark for student $student_id, task $task_id: " . $stmt->error);
    }
}

$stmt->close();
$conn->close();

if ($error_count > 0) {
    echo json_encode([
        "success" => $success_count > 0, 
        "message" => "Saved $success_count marks successfully, $error_count failed",
        "saved_count" => $success_count,
        "error_count" => $error_count
    ]);
} else {
    echo json_encode([
        "success" => true, 
        "message" => "All $success_count marks saved successfully",
        "saved_count" => $success_count
    ]);
}
?>