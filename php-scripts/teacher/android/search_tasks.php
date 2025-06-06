<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_connect.php';

$type    = isset($_GET['type'])    ? $_GET['type'] : null;
$subject = isset($_GET['subject']) ? $_GET['subject'] : null;
$grade   = isset($_GET['grade'])   ? $_GET['grade']   : null;
$search  = isset($_GET['search'])  ? trim($_GET['search']) : null;

$sql = "SELECT 
            task.id,
            s.title AS subject_title,
            s.grade_number,
            t.name AS teacher_name,
            task.max_score,
            task.date,
            a.deadline,
            a.question_file_url,
            task.type,
            CASE 
                WHEN a.id IS NOT NULL THEN 'assignment'
                ELSE 'exam'
            END AS task_type
        FROM Task task
        LEFT JOIN Assignment a ON task.id = a.id
        INNER JOIN Subject s   ON task.subject_id = s.id
        INNER JOIN Teacher t   ON task.teacher_id = t.id
        WHERE 1=1";

$params = [];
$types = "";

// Filter by type (assignment/exam)
if ($type && in_array($type, ['assignment', 'exam'])) {
    if ($type === 'assignment') {
        $sql .= " AND a.id IS NOT NULL";
    } else {
        $sql .= " AND a.id IS NULL";
    }
}

// Filter by subject
if ($subject && $subject !== 'All Subjects') {
    $sql .= " AND s.title = ?";
    $types .= 's';
    $params[] = $subject;
}

// Filter by grade
if ($grade && $grade !== 'All Grades') {
    // Extract number from "Grade X" format if needed
    $gradeNum = is_numeric($grade) ? (int)$grade : (int)str_replace('Grade ', '', $grade);
    $sql .= " AND s.grade_number = ?";
    $types .= 'i';
    $params[] = $gradeNum;
}

// Filter by search (subject title or teacher name)
if ($search && !empty($search)) {
    $sql .= " AND (s.title LIKE ? OR t.name LIKE ?)";
    $types .= 'ss';
    $searchParam = '%' . $search . '%';
    $params[] = $searchParam;
    $params[] = $searchParam;
}

$sql .= " ORDER BY task.date DESC";

$stmt = $conn->prepare($sql);
if ($stmt === false) {
    http_response_code(500);
    echo json_encode(['error' => 'Failed to prepare statement: ' . $conn->error]);
    exit();
}

// Bind parameters if any
if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}

if (!$stmt->execute()) {
    http_response_code(500);
    echo json_encode(['error' => 'Failed to execute statement: ' . $stmt->error]);
    exit();
}

$result = $stmt->get_result();

$tasks = [];
while ($row = $result->fetch_assoc()) {
    $tasks[] = [
        'id'                => (int)$row['id'],
        'subject_title'     => $row['subject_title'],
        'grade_number'      => (int)$row['grade_number'],
        'teacher_name'      => $row['teacher_name'],
        'max_score'         => (float)$row['max_score'],
        'date'              => $row['date'],
        'deadline'          => $row['deadline'], // Will be null for exams
        'question_file_url' => $row['question_file_url'], // Will be null for exams
        'type'              => $row['task_type'], // "assignment" or "exam"
    ];
}

echo json_encode($tasks);

$stmt->close();
$conn->close();
?>