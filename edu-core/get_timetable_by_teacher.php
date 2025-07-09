<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Check for POST data
$input = json_decode(file_get_contents("php://input"), true);
if (!isset($input['teacher_id'])) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Missing teacher_id"]);
    exit();
}

$teacher_id = $input['teacher_id'];

try {
    // Query to fetch DISTINCT timetable entries with proper grade information
    $sql = "
        SELECT DISTINCT
            t.subject_id,
            s.title AS subject_name,
            t.class_id,
            t.class_id AS class_grade_id,
            s.grade_number,
            CONCAT('Grade ', s.grade_number) AS class_grade_name
        FROM TimeTable t
        INNER JOIN Subject s ON t.subject_id = s.id
        WHERE t.teacher_id = ?
        ORDER BY s.grade_number, s.title
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$teacher_id]);
    $timetable = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "timetable" => $timetable,
        "count" => count($timetable)
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Database error: " . $e->getMessage()]);
}
?>
