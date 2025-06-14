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

    // 1. Check if the teacher is assigned to the subject and class
    $teacherSubjectCheckQuery = "SELECT 1
                                 FROM Teacher t
                                 JOIN Subject s ON t.subject_id = s.id
                                 JOIN Classroom c ON s.grade_number = c.grade_number
                                 WHERE t.id = :teacher_id AND s.id = :subject_id AND c.id = :class_id";

    $teacherSubjectStmt = $pdo->prepare($teacherSubjectCheckQuery);
    $teacherSubjectStmt->bindParam(':teacher_id', $teacher_id);
    $teacherSubjectStmt->bindParam(':subject_id', $subject_id);
    $teacherSubjectStmt->bindParam(':class_id', $class_id);
    $teacherSubjectStmt->execute();

    if ($teacherSubjectStmt->rowCount() == 0) {
        return false;  // Teacher is not assigned to the subject for this class
    }

    // 2. Check if the selected time slot is available (no conflict)
    $timeSlotCheckQuery = "SELECT * FROM TimeTable 
                           WHERE class_id = :class_id AND subject_id = :subject_id 
                           AND teacher_id = :teacher_id AND day_of_week = :day_of_week 
                           AND ((start_time <= :end_time AND end_time >= :start_time))";

    $timeSlotStmt = $pdo->prepare($timeSlotCheckQuery);
    $timeSlotStmt->bindParam(':class_id', $class_id);
    $timeSlotStmt->bindParam(':subject_id', $subject_id);
    $timeSlotStmt->bindParam(':teacher_id', $teacher_id);
    $timeSlotStmt->bindParam(':day_of_week', $day_of_week);
    $timeSlotStmt->bindParam(':start_time', $start_time);
    $timeSlotStmt->bindParam(':end_time', $end_time);
    $timeSlotStmt->execute();

    $result = $timeSlotStmt->fetch(PDO::FETCH_ASSOC);
    return !$result;  // Return true if no conflict found (i.e., time is available)
}

// Check time availability
$isAvailable = checkTimeAvailability($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time);

// Return the result as JSON
echo json_encode(['status' => 'success', 'available' => $isAvailable]);
?>
