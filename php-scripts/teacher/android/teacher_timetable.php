<?php
// teacher_timetable.php - Fetch teacher's timetable for populating filters
header('Content-Type: application/json');

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    require_once 'db_connect.php';
    
    $teacher_id = $_GET['teacher_id'] ?? null;
    
    // Log received parameters
    error_log("teacher_timetable.php - Received teacher_id: $teacher_id");
    
    if (!$teacher_id) {
        http_response_code(400);
        echo json_encode(["error" => "teacher_id is required"]);
        exit;
    }
    
    // Check database connection
    if (!$conn) {
        throw new Exception("Database connection failed");
    }
    
    // Query to get teacher's timetable with subjects and classes
    $sql = "
    SELECT DISTINCT
        tt.subject_id,
        s.title AS subject_title,
        s.grade_number,
        tt.class_id
    FROM TimeTable tt
    JOIN Subject s ON tt.subject_id = s.id
    WHERE tt.teacher_id = ?
    ORDER BY s.title, s.grade_number
    ";
    
    error_log("SQL Query: " . $sql);
    
    $stmt = $conn->prepare($sql);
    if ($stmt === false) {
        throw new Exception("Prepare failed: " . $conn->error);
    }
    
    $stmt->bind_param('i', $teacher_id);
    
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: " . $stmt->error);
    }
    
    $result = $stmt->get_result();
    
    $timetable = [];
    while ($row = $result->fetch_assoc()) {
        $timetable[] = [
            'subject_id' => (int)$row['subject_id'],
            'subject_title' => $row['subject_title'],
            'grade_number' => (int)$row['grade_number'],
            'class_id' => (int)$row['class_id']
        ];
    }
    
    error_log("Found " . count($timetable) . " timetable entries");
    
    // Return the timetable data
    echo json_encode($timetable);
    
} catch (Exception $e) {
    error_log("Error in teacher_timetable.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>