<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_connect.php';  // use your existing MySQLi connection

$sql = "SELECT grade_number FROM ClassGrade ORDER BY grade_number ASC";

$result = $conn->query($sql);

if (!$result) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error',
        'message' => 'Failed to retrieve grades'
    ]);
    error_log("Database error in get_grades.php: " . $conn->error);
    exit();
}

$grades = [];

while ($row = $result->fetch_assoc()) {
    $grades[] = [
        'grade_number' => (int)$row['grade_number']
    ];
}

echo json_encode($grades);

$conn->close();
?>
