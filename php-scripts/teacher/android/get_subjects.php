<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_connect.php';  // Use your mysqli connection

$sql = "SELECT DISTINCT 
            s.id,
            s.title,
            s.grade_number,
            s.semester_number
        FROM Subject s
        ORDER BY s.grade_number ASC, s.title ASC";

$result = $conn->query($sql);

if (!$result) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error',
        'message' => 'Failed to retrieve subjects'
    ]);
    error_log("Database error in get_subjects.php: " . $conn->error);
    exit();
}

$subjects = [];

while ($row = $result->fetch_assoc()) {
    $subjects[] = [
        'id' => (int)$row['id'],
        'title' => $row['title'],
        'grade_number' => (int)$row['grade_number'],
        'semester_number' => (int)$row['semester_number']
    ];
}

echo json_encode($subjects);

// Optional: close connection
$conn->close();
?>
