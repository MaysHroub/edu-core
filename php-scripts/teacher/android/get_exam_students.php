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

// First retrieve the class_id from Task
$sqlClass = "SELECT class_id FROM Task WHERE id = ?";
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
$stmtClass->close();

// Now fetch all students in that class
$sql = "SELECT 
            s.id AS student_id,
            s.name AS student_name,
            tr.mark,
            tr.feedback
        FROM Student s
        LEFT JOIN TaskResult tr ON tr.task_id = ? AND tr.student_id = s.id
        WHERE s.class_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param('ii', $taskId, $classId);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
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
$stmt->close();
$conn->close();
?>