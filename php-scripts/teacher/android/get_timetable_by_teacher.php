<?php
header('Content-Type: application/json');
require_once 'db_connect.php';

// Check for POST data
$input = json_decode(file_get_contents("php://input"), true);
if (!isset($input['teacher_id'])) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Missing teacher_id"]);
    exit();
}

$teacher_id = $input['teacher_id'];

// Query to fetch timetable entries with all required data
$sql = "
    SELECT 
        t.subject_id,
        s.title AS subject_name,
        t.class_id,
        t.class_id AS class_grade_id,
        CONCAT('Class ', t.class_id) AS class_grade_name,
        s.grade_number,
        t.day_of_week,
        t.start_time,
        t.end_time
    FROM TimeTable t
    INNER JOIN Subject s ON t.subject_id = s.id
    WHERE t.teacher_id = ?
    ORDER BY t.day_of_week, t.start_time
";

if (!$conn) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

$stmt = $conn->prepare($sql);
if (!$stmt) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Prepare statement failed: " . $conn->error]);
    exit();
}

$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$timetable = [];
while ($row = $result->fetch_assoc()) {
    $timetable[] = $row;
}

$stmt->close();
$conn->close();

echo json_encode([
    "success" => true,
    "timetable" => $timetable,
    "count" => count($timetable)
]);
?>