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
    $type = isset($input['type']) ? $input['type'] : 'Assignment';
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
    $pdo->beginTransaction();

    try {
        // Insert into Task table with description
        $stmt = $pdo->prepare("INSERT INTO Task (subject_id, class_id, teacher_id, type, max_score, description, title) VALUES (?, ?, ?, ?, ?, ?, ?)");
        $stmt->execute([$subjectId, $classId, $teacherId, $type, $maxScore, $description, $title]);
        
        $taskId = $pdo->lastInsertId();

        // Update file URL to include assignment ID (move file to proper folder)
        $oldFileUrl = $questionFileUrl;
        $newFileUrl = updateFilePathWithAssignmentId($oldFileUrl, $taskId);

        // Insert into Assignment table with updated file URL
        $stmt2 = $pdo->prepare("INSERT INTO Assignment (id, question_file_url, deadline) VALUES (?, ?, ?)");
        $stmt2->execute([$taskId, $newFileUrl, $deadline]);

        // Commit transaction
        $pdo->commit();
        
        echo json_encode([
            "success" => true, 
            "task_id" => $taskId,
            "assignment_id" => $taskId,
            "message" => "Assignment published successfully"
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

function updateFilePathWithAssignmentId($oldUrl, $assignmentId) {
    // If file was uploaded to temp folder, move it to assignment-specific folder
    if (strpos($oldUrl, 'uploads/assignments/temp/') === 0) {
        $fileName = basename($oldUrl);
        $newDir = 'uploads/assignments/' . $assignmentId . '/';
        $newUrl = $newDir . $fileName;
        
        // Create new directory
        if (!file_exists($newDir)) {
            mkdir($newDir, 0777, true);
        }
        
        // Move file
        if (file_exists($oldUrl)) {
            rename($oldUrl, $newUrl);
            // Remove temp directory if empty
            $tempDir = dirname($oldUrl);
            if (is_dir($tempDir) && count(scandir($tempDir)) == 2) { // Only . and ..
                rmdir($tempDir);
            }
        }
        
        return $newUrl;
    }
    
    return $oldUrl; // Return original if not in temp folder
}
?>
