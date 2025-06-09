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
    $subjectId = isset($input['subject_id']) ? intval($input['subject_id']) : null;
    $classId = isset($input['class_id']) ? intval($input['class_id']) : null;
    $teacherId = isset($input['teacher_id']) ? intval($input['teacher_id']) : null;
    $type = isset($input['type']) ? $input['type'] : 'assignment';
    $maxScore = isset($input['max_score']) ? floatval($input['max_score']) : null;
    $title = isset($input['title']) ? $input['title'] : "";
    $description = isset($input['description']) ? $input['description'] : "";
    $questionFileUrl = isset($input['question_file_url']) ? $input['question_file_url'] : "";
    $deadline = isset($input['deadline']) ? $input['deadline'] : null;

    // Validate required fields
    if (!$subjectId || !$classId || !$teacherId || !$maxScore || !$questionFileUrl || !$deadline) {
        http_response_code(400);
        echo json_encode(["error" => "Missing required fields"]);
        exit();
    }

    // Start transaction
    $conn->begin_transaction();

    try {
        // Insert into Task table with description
        $stmt = $conn->prepare("INSERT INTO Task (subject_id, class_id, teacher_id, type, max_score, description, title) VALUES (?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("iiisdss", $subjectId, $classId, $teacherId, $type, $maxScore, $description, $title);
        
        if (!$stmt->execute()) {
            throw new Exception("Failed to insert task");
        }

        $taskId = $conn->insert_id;

        // Insert into Assignment table
        $stmt2 = $conn->prepare("INSERT INTO Assignment (id, question_file_url, deadline) VALUES (?, ?, ?)");
        $stmt2->bind_param("iss", $taskId, $questionFileUrl, $deadline);
        
        if (!$stmt2->execute()) {
            throw new Exception("Failed to insert assignment");
        }

        // Commit transaction
        $conn->commit();
        
        echo json_encode([
            "success" => true, 
            "task_id" => $taskId,
            "message" => "Assignment published successfully"
        ]);

    } catch (Exception $e) {
        // Rollback transaction on error
        $conn->rollback();
        throw $e;
    }

    $stmt->close();
    if (isset($stmt2)) $stmt2->close();
    $conn->close();

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Server error: " . $e->getMessage()]);
}
?>