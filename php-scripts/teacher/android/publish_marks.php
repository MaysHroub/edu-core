<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["success" => false, "message" => "Method not allowed. Use POST."]);
    exit();
}

// Include DB connection
require_once 'db_connect.php';

// Read the incoming JSON payload
$input = json_decode(file_get_contents("php://input"), true);

// Check if JSON parsing was successful
if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Invalid JSON format: " . json_last_error_msg()]);
    exit();
}

// Check for required data - the Android app sends "marks" array
if (!isset($input['marks']) || !is_array($input['marks'])) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Invalid or incomplete input. Expected 'marks' array."]);
    exit();
}

$marks = $input['marks']; // array of { task_id, student_id, mark, feedback }

if (empty($marks)) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Marks array is empty."]);
    exit();
}

// Start transaction for data consistency
$conn->autocommit(FALSE);

try {
    // Prepare statement for REPLACE INTO TaskResult
    $stmt = $conn->prepare("REPLACE INTO TaskResult (task_id, student_id, mark, feedback) VALUES (?, ?, ?, ?)");

    if (!$stmt) {
        throw new Exception("Failed to prepare statement: " . $conn->error);
    }

    $success_count = 0;
    $error_details = [];

    // Process each mark entry
    foreach ($marks as $index => $mark_entry) {
        // Validate required fields
        if (!isset($mark_entry['task_id']) || !isset($mark_entry['student_id'])) {
            $error_details[] = "Entry $index: Missing task_id or student_id";
            continue;
        }

        $task_id = intval($mark_entry['task_id']);
        $student_id = trim($mark_entry['student_id']);
        $mark = isset($mark_entry['mark']) && $mark_entry['mark'] !== null && $mark_entry['mark'] !== '' ? floatval($mark_entry['mark']) : null;
        $feedback = isset($mark_entry['feedback']) ? trim($mark_entry['feedback']) : null;

        // Validate task_id and student_id are not empty
        if ($task_id <= 0 || empty($student_id)) {
            $error_details[] = "Entry $index: Invalid task_id ($task_id) or student_id ($student_id)";
            continue;
        }

        // Validate mark range (assuming 0-100 scale)
        if ($mark !== null && ($mark < 0 || $mark > 100)) {
            $error_details[] = "Entry $index: Mark ($mark) must be between 0 and 100";
            continue;
        }

        // Bind parameters (i = integer, s = string, d = double)
        $stmt->bind_param("isds", $task_id, $student_id, $mark, $feedback);
        
        if ($stmt->execute()) {
            $success_count++;
        } else {
            $error_details[] = "Entry $index: Database error - " . $stmt->error;
        }
    }

    $stmt->close();

    // Check if we had any successful saves
    if ($success_count > 0) {
        // Commit the transaction
        $conn->commit();
        
        if (!empty($error_details)) {
            // Partial success
            echo json_encode([
                "success" => true, 
                "message" => "Saved $success_count marks successfully, " . count($error_details) . " failed",
                "saved_count" => $success_count,
                "error_count" => count($error_details),
                "errors" => $error_details
            ]);
        } else {
            // Complete success
            echo json_encode([
                "success" => true, 
                "message" => "All $success_count marks published successfully!",
                "saved_count" => $success_count
            ]);
        }
    } else {
        // No successful saves, rollback
        $conn->rollback();
        echo json_encode([
            "success" => false, 
            "message" => "Failed to save any marks",
            "error_count" => count($error_details),
            "errors" => $error_details
        ]);
    }

} catch (Exception $e) {
    // Rollback transaction on any exception
    $conn->rollback();
    error_log("publish_marks.php error: " . $e->getMessage());
    
    http_response_code(500);
    echo json_encode([
        "success" => false, 
        "message" => "Server error: " . $e->getMessage()
    ]);
} finally {
    // Restore autocommit and close connection
    $conn->autocommit(TRUE);
    $conn->close();
}
?>