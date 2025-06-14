<?php
require_once 'connection.php';  // Include the database connection

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if class_id is provided in the JSON input
if (!isset($input['class_id'])) {
    echo json_encode(['error' => 'Class ID is required']);
    exit();
}

$class_id = $input['class_id'];

/**
 * Fetch the timetable for a specific class.
 *
 * @param int $class_id The class ID to fetch the timetable for.
 * @return array List of timetable entries for the class.
 */
function getClassTimetable($class_id) {
    global $pdo;

    // Query to fetch the timetable for the class
    $query = "SELECT c.grade_number, c.section, t.fname AS teacher_fname, t.lname AS teacher_lname,
                     s.title AS subject, tt.day_of_week, tt.start_time, tt.end_time
              FROM TimeTable tt
              JOIN Classroom c ON tt.class_id = c.id
              JOIN Teacher t ON tt.teacher_id = t.id
              JOIN Subject s ON tt.subject_id = s.id
              WHERE tt.class_id = :class_id
              ORDER BY tt.day_of_week, tt.start_time";

    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':class_id', $class_id);
    $stmt->execute();

    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Fetch the timetable for the class
$timetable = getClassTimetable($class_id);

// Return the result as JSON
if ($timetable) {
    echo json_encode(['status' => 'success', 'timetable' => $timetable]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'No timetable found for the class']);
}
?>
