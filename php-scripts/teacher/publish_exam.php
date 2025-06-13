<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

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
    $maxScore = isset($input['max_score']) ? floatval($input['max_score']) : null;
    $title = isset($input['title']) ? $input['title'] : "";
    $description = isset($input['description']) ? $input['description'] : "";
    $examType = isset($input['exam_type']) ? $input['exam_type'] : "";
    $date = isset($input['date']) ? $input['date'] : null;

    // Validate required fields for exam
    if (!$subjectId || !$classId || !$teacherId || !$maxScore || empty($title) || empty($examType)) {
        http_response_code(400);
        echo json_encode(["error" => "Missing required fields: subject_id, class_id, teacher_id, max_score, title, exam_type"]);
        exit();
    }

    // Start transaction
    $pdo->beginTransaction();

    try {
        $fullDescription = "Exam Type: " . $examType . "\n\n" . $description;
        
        $stmt = $pdo->prepare("INSERT INTO Task (subject_id, class_id, teacher_id, type, title, max_score, description, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->execute([$subjectId, $classId, $teacherId, $examType, $title, $maxScore, $fullDescription, $date]);
        
        $taskId = $pdo->lastInsertId();

        // Commit transaction
        $pdo->commit();
        
        echo json_encode([
            "success" => true, 
            "task_id" => $taskId,
            "message" => "Exam announced successfully"
        ]);

    } catch (Exception $e) {
        // Rollback transaction on error
        $pdo->rollback();
        throw $e;
    }

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Server error: " . $e->getMessage()]);
}
?>
