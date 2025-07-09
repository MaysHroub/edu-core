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
    // First retrieve the class_id and deadline from Task and Assignment
    $sqlClass = "SELECT t.class_id, a.deadline 
                 FROM Task t 
                 LEFT JOIN Assignment a ON t.id = a.id 
                 WHERE t.id = ?";
    $stmtClass = $pdo->prepare($sqlClass);
    $stmtClass->execute([$taskId]);
    $rowClass = $stmtClass->fetch(PDO::FETCH_ASSOC);
    
    if (!$rowClass) {
        echo json_encode([]);
        exit();
    }
    
    $classId = $rowClass['class_id'];
    $deadline = $rowClass['deadline'];

    // Now fetch all students in that class with their submission data
    $sql = "SELECT 
                s.id AS student_id,
                CONCAT(s.fname, ' ', s.lname) AS student_name,
                tr.mark,
                tr.feedback,
                asub.submission_date,
                asub.submission_file_url
            FROM Student s
            LEFT JOIN TaskResult tr ON tr.task_id = ? AND tr.student_id = s.id
            LEFT JOIN AssignmentSubmission asub ON asub.id = ? AND asub.student_id = s.id
            WHERE s.class_id = ?
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$taskId, $taskId, $classId]);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $students = [];
    $currentDate = date('Y-m-d');

    foreach ($results as $row) {
        // Determine status
        $status = 'pending';
        if ($row['submission_date'] !== null) {
            $status = 'submitted';
        } elseif ($deadline !== null && $currentDate > $deadline) {
            $status = 'overdue';
        }
        
        $students[] = [
            'student_id' => $row['student_id'],
            'student_name' => $row['student_name'],
            'submission_date' => $row['submission_date'],
            'submission_file_url' => $row['submission_file_url'],
            'mark' => $row['mark'] !== null ? (float)$row['mark'] : null,
            'feedback' => $row['feedback'],
            'status' => $status
        ];
    }

    echo json_encode($students);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error occurred']);
}
?>