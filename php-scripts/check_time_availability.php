<?php
require_once 'connection.php';  // Include the database connection

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if required parameters are provided in the JSON input
if (!isset($input['class_id'], $input['subject_id'], $input['teacher_id'], $input['day_of_week'], $input['start_time'], $input['end_time'])) {
    echo json_encode(['error' => 'All parameters are required']);
    exit();
}

// Extract the input parameters
$class_id = $input['class_id'];
$subject_id = $input['subject_id'];
$teacher_id = $input['teacher_id'];
$day_of_week = $input['day_of_week'];
$start_time = $input['start_time'];
$end_time = $input['end_time'];

/**
 * Check if a time slot is available for a specific subject and teacher.
 *
 * @param int $class_id The class ID.
 * @param int $subject_id The subject ID.
 * @param int $teacher_id The teacher ID.
 * @param string $day_of_week The day of the week.
 * @param string $start_time The start time.
 * @param string $end_time The end time.
 * @return bool Whether the time slot is available.
 */
function checkTimeAvailability($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time) {
    global $pdo;
    
    $query = "SELECT * FROM TimeTable WHERE class_id = :class_id AND subject_id = :subject_id 
              AND teacher_id = :teacher_id AND day_of_week = :day_of_week 
              AND ((start_time <= :end_time AND end_time >= :start_time))";

    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':class_id', $class_id);
    $stmt->bindParam(':subject_id', $subject_id);
    $stmt->bindParam(':teacher_id', $teacher_id);
    $stmt->bindParam(':day_of_week', $day_of_week);
    $stmt->bindParam(':start_time', $start_time);
    $stmt->bindParam(':end_time', $end_time);
    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    return !$result;  // Return true if no conflict found (i.e., time is available)
}

// Check time availability
$isAvailable = checkTimeAvailability($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time);

// Return the result as JSON
echo json_encode(['status' => 'success', 'available' => $isAvailable]);
?>
