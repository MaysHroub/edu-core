<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

try {
    // Get and validate parameters
    $teacher_id = filter_input(INPUT_GET, 'teacher_id', FILTER_VALIDATE_INT);
    $search = filter_input(INPUT_GET, 'search', FILTER_SANITIZE_STRING) ?? '';
    $subject_id = filter_input(INPUT_GET, 'subject_id', FILTER_VALIDATE_INT);
    $class_id = filter_input(INPUT_GET, 'class_id', FILTER_VALIDATE_INT);

    if (!$teacher_id) {
        throw new Exception("Valid teacher_id is required");
    }

    // Base query
    $sql = "
    SELECT DISTINCT
        a.id,
        CONCAT('Assignment ', a.id) AS assignment_title,
        s.title AS subject_title,
        s.grade_number,
        DATE_FORMAT(a.deadline, '%Y-%m-%d') AS deadline,
        (
            SELECT COUNT(*)
            FROM AssignmentSubmission sub
            WHERE sub.id = a.id AND sub.submission_file_url IS NOT NULL
        ) as submitted_count,
        (
            SELECT COUNT(*)
            FROM Student st
            WHERE st.class_id = t.class_id
        ) as total_students
    FROM Assignment a
    JOIN Task t ON a.id = t.id
    JOIN Subject s ON t.subject_id = s.id
    WHERE t.teacher_id = ?
    ";

    $params = [$teacher_id];
    $types = 'i';

    // Add filters
    if (!empty($search)) {
        $sql .= " AND s.title LIKE ?";
        $params[] = "%$search%";
        $types .= 's';
    }

    if ($subject_id) {
        $sql .= " AND s.id = ?";
        $params[] = $subject_id;
        $types .= 'i';
    }

    if ($class_id) {
        $sql .= " AND t.class_id = ?";
        $params[] = $class_id;
        $types .= 'i';
    }

    $sql .= " ORDER BY a.deadline ASC, a.id DESC";

    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception("Query preparation failed: " . $conn->error);
    }

    if (!empty($params)) {
        $stmt->bind_param($types, ...$params);
    }

    if (!$stmt->execute()) {
        throw new Exception("Query execution failed: " . $stmt->error);
    }

    $result = $stmt->get_result();
    $assignments = [];

    while ($row = $result->fetch_assoc()) {
        $assignments[] = [
            'id' => (int)$row['id'],
            'assignment_title' => $row['assignment_title'],
            'subject_title' => $row['subject_title'],
            'grade_number' => (int)$row['grade_number'],
            'deadline' => $row['deadline'],
            'submitted_count' => (int)$row['submitted_count'],
            'total_students' => (int)$row['total_students']
        ];
    }

    echo json_encode($assignments);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'error' => $e->getMessage()
    ]);
} finally {
    if (isset($stmt)) {
        $stmt->close();
    }
    if (isset($conn)) {
        $conn->close();
    }
}
?> 