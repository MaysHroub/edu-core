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
 * Check if a teacher is assigned to the subject for the class
 */
function checkTeacherSubjectAssignment($teacher_id, $subject_id, $class_id) {
    global $pdo;
    
    $query = "SELECT 1
              FROM Teacher t
              JOIN Subject s ON t.subject_id = s.id
              JOIN Classroom c ON s.grade_number = c.grade_number
              WHERE t.id = :teacher_id AND s.id = :subject_id AND c.id = :class_id";

    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':teacher_id', $teacher_id);
    $stmt->bindParam(':subject_id', $subject_id);
    $stmt->bindParam(':class_id', $class_id);
    $stmt->execute();

    return $stmt->rowCount() > 0;
}

/**
 * Check if a time slot is available for a specific subject and teacher.
 */
function checkTimeAvailability($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time) {
    global $pdo;

    // Check if the teacher is assigned to the subject for the class
    if (!checkTeacherSubjectAssignment($teacher_id, $subject_id, $class_id)) {
        return json_encode(['error' => 'The teacher is not assigned to this subject for the class']);
    }

    // Query to check if the time slot is already taken
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

/**
 * Add a new entry to the timetable.
 */
function addToTimetable($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time) {
    global $pdo;

    // Check if the time slot is available
    $isAvailable = checkTimeAvailability($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time);

    if ($isAvailable === true) {
        // Insert the new timetable entry
        $query = "INSERT INTO TimeTable (class_id, subject_id, teacher_id, day_of_week, start_time, end_time)
                  VALUES (:class_id, :subject_id, :teacher_id, :day_of_week, :start_time, :end_time)";

        $stmt = $pdo->prepare($query);
        $stmt->bindParam(':class_id', $class_id);
        $stmt->bindParam(':subject_id', $subject_id);
        $stmt->bindParam(':teacher_id', $teacher_id);
        $stmt->bindParam(':day_of_week', $day_of_week);
        $stmt->bindParam(':start_time', $start_time);
        $stmt->bindParam(':end_time', $end_time);
        $stmt->execute();

        return json_encode(['status' => 'success', 'message' => 'Timetable entry added successfully']);
    } else {
        return json_encode(['status' => 'error', 'message' => 'Time slot not available']);
    }
}

// Add to the timetable
$response = addToTimetable($class_id, $subject_id, $teacher_id, $day_of_week, $start_time, $end_time);
echo $response;
?>
