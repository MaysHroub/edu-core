<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_connect.php';

try {
    // Decode JSON from the request body
    $input = json_decode(file_get_contents("php://input"), true);

    if (!$input) {
        http_response_code(400);
        echo json_encode(["error" => "Invalid JSON input"]);
        exit();
    }

    // Extract values safely
    $subjectId   = isset($input['subject_id']) ? intval($input['subject_id']) : null;
    $classId     = isset($input['class_id']) ? intval($input['class_id']) : null;
    $teacherId   = isset($input['teacher_id']) ? intval($input['teacher_id']) : null;
    $type        = isset($input['type']) ? $input['type'] : null;
    $maxScore    = isset($input['max_score']) ? floatval($input['max_score']) : 100;
    $date        = isset($input['date']) ? $input['date'] : null;
    $title       = isset($input['title']) ? $input['title'] : "";
    $description = isset($input['description']) ? $input['description'] : "";

    if (!$subjectId || !$classId || !$teacherId || !$type || !$date) {
        http_response_code(400);
        echo json_encode(["error" => "Missing required fields"]);
        exit();
    }

    // Insert into Task table
    $stmt = $conn->prepare("INSERT INTO Task (subject_id, class_id, teacher_id, type, max_score, date, title, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("iiisdsss", $subjectId, $classId, $teacherId, $type, $maxScore, $date, $title, $description);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "task_id" => $stmt->insert_id]);
    } else {
        http_response_code(500);
        echo json_encode(["error" => "Failed to insert task"]);
    }

    $stmt->close();
    $conn->close();
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Server error: " . $e->getMessage()]);
}
?>
