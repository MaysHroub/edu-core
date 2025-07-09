<?php
// Include the database connection file
header('Content-Type: application/json');
require_once 'connection.php';

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if teacher_id is provided in the JSON input
if (!isset($input['teacher_id'])) {
    echo json_encode(['error' => 'Teacher ID is required']);
    exit();
}

$teacherId = $input['teacher_id'];

function getTeacherTimetable($teacherId) {
    global $pdo;
    
    // Verify teacher exists
    $teacherSql = "SELECT id FROM Teacher WHERE id = :teacher_id";
    $stmt = $pdo->prepare($teacherSql);
    $stmt->execute(['teacher_id' => $teacherId]);
    $teacher = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$teacher) {
        return null; // Teacher not found
    }
    
    // Retrieve the timetable for this teacher
    $sql = "SELECT t.day_of_week, t.start_time, t.end_time, sub.title AS subject, 
                   CONCAT(c.grade_number, c.section) AS class_name,
                   c.id AS class_id
            FROM TimeTable t
            JOIN Subject sub ON t.subject_id = sub.id
            JOIN Classroom c ON t.class_id = c.id
            WHERE t.teacher_id = :teacher_id
            ORDER BY FIELD(t.day_of_week, 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday'), t.start_time";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute(['teacher_id' => $teacherId]);
    $timetable = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    return $timetable;
}

// Fetch timetable
$timetable = getTeacherTimetable($teacherId);

if ($timetable) {
    // Organize timetable by days of the week
    $daysOfWeek = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday'];
    $organizedTimetable = [];
    
    foreach ($daysOfWeek as $day) {
        $organizedTimetable[$day] = [];
    }
    
    foreach ($timetable as $entry) {
        $organizedTimetable[$entry['day_of_week']][] = [
            'time' => $entry['start_time'] . ' - ' . $entry['end_time'],
            'subject' => $entry['subject'],
            'class' => $entry['class_name'],
            'class_id' => $entry['class_id']
        ];
    }
    
    // Remove empty days from the response
    foreach ($organizedTimetable as $day => $schedule) {
        if (empty($schedule)) {
            unset($organizedTimetable[$day]);
        }
    }
    
    // Return response
    echo json_encode(['status' => 'success', 'timetable' => $organizedTimetable]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'No timetable found']);
}
?>