<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_connect.php';

$taskId = isset($_GET['taskId']) ? intval($_GET['taskId']) : -1;
if ($taskId < 0) {
    echo json_encode([]);
    exit();
}

// First retrieve the class_id and deadline from Task and Assignment
$sqlClass = "SELECT t.class_id, a.deadline 
             FROM Task t 
             LEFT JOIN Assignment a ON t.id = a.id 
             WHERE t.id = ?";
$stmtClass = $conn->prepare($sqlClass);
$stmtClass->bind_param('i', $taskId);
$stmtClass->execute();
$resClass = $stmtClass->get_result();
if ($resClass->num_rows == 0) {
    echo json_encode([]);
    exit();
}
$rowClass = $resClass->fetch_assoc();
$classId = $rowClass['class_id'];
$deadline = $rowClass['deadline'];
$stmtClass->close();

// Now fetch all students in that class with their submission data
$sql = "SELECT 
            s.id AS student_id,
            s.name AS student_name,
            tr.mark,
            tr.feedback,
            asub.submission_date,
            asub.submission_file_url
        FROM Student s
        LEFT JOIN TaskResult tr ON tr.task_id = ? AND tr.student_id = s.id
        LEFT JOIN AssignmentSubmission asub ON asub.id = ? AND asub.student_id = s.id
        WHERE s.class_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param('iii', $taskId, $taskId, $classId);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
$currentDate = date('Y-m-d');

while ($row = $result->fetch_assoc()) {
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
$stmt->close();
$conn->close();
?>