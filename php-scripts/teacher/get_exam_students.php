<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

$taskId = isset($_GET['taskId']) ? intval($_GET['taskId']) : -1;
if ($taskId < 0) {
    echo json_encode([]);
    exit();
}

try {
    // First retrieve the class_id from Task
    $sqlClass = "SELECT class_id FROM Task WHERE id = ?";
    $stmtClass = $pdo->prepare($sqlClass);
    $stmtClass->execute([$taskId]);
    $rowClass = $stmtClass->fetch(PDO::FETCH_ASSOC);
    
    if (!$rowClass) {
        echo json_encode([]);
        exit();
    }
    
    $classId = $rowClass['class_id'];

    // Now fetch all students in that class
    $sql = "SELECT 
                s.id AS student_id,
                CONCAT(s.fname, ' ', s.lname) AS student_name,
                tr.mark,
                tr.feedback
            FROM Student s
            LEFT JOIN TaskResult tr ON tr.task_id = ? AND tr.student_id = s.id
            WHERE s.class_id = ?
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$taskId, $classId]);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $students = [];
    foreach ($results as $row) {
        $students[] = [
            'student_id' => $row['student_id'],
            'student_name' => $row['student_name'],
            'submission_date' => null,  // Exams don't have submission dates
            'submission_file_url' => null,  // Exams don't have submission files
            'mark' => $row['mark'] !== null ? (float)$row['mark'] : null,
            'feedback' => $row['feedback'],
            'status' => 'exam'  // Or null, depending on your adapter logic
        ];
    }

    echo json_encode($students);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>