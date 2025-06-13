<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

$type    = isset($_GET['type'])    ? strtolower(trim($_GET['type'])) : null;
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
            task.type
        FROM Task task
        LEFT JOIN Assignment a ON task.id = a.id
        INNER JOIN Subject s   ON task.subject_id = s.id
        INNER JOIN Teacher t   ON task.teacher_id = t.id
        WHERE 1=1";

$params = [];

// Filter by specific type
if ($type && $type !== 'all types') {
    if ($type === 'assignment') {
        // Show only assignments
        $sql .= " AND a.id IS NOT NULL";
    } else {
        // Show only exams with specific type
        $sql .= " AND a.id IS NULL AND LOWER(task.type) = ?";
        $params[] = $type;
    }
}

// Filter by subject
if ($subject && $subject !== 'All Subjects') {
    $sql .= " AND s.title = ?";
    $params[] = $subject;
}

// Filter by grade
if ($grade && $grade !== 'All Grades') {
    // Extract number from "Grade X" format if needed
    $gradeNum = is_numeric($grade) ? (int)$grade : (int)str_replace('Grade ', '', $grade);
    $sql .= " AND s.grade_number = ?";
    $params[] = $gradeNum;
}

// Filter by search (subject title or teacher name)
if ($search && !empty($search)) {
    $sql .= " AND (s.title LIKE ? OR t.name LIKE ?)";
    $searchParam = '%' . $search . '%';
    $params[] = $searchParam;
    $params[] = $searchParam;
}

$sql .= " ORDER BY task.date DESC";

try {
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $tasks = [];
    foreach ($results as $row) {
        $tasks[] = [
            'id'                => (int)$row['id'],
            'subject_title'     => $row['subject_title'],
            'grade_number'      => (int)$row['grade_number'],
            'teacher_name'      => $row['teacher_name'],
            'max_score'         => (float)$row['max_score'],
            'date'              => $row['date'],
            'deadline'          => $row['deadline'], // Will be null for exams
            'question_file_url' => $row['question_file_url'], // Will be null for exams
            'type'              => $row['type'], // Return the actual type from database
        ];
    }

    echo json_encode($tasks);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>
