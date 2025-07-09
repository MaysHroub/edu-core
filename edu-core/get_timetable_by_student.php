<?php
// Include the database connection file
header('Content-Type: application/json');
require_once 'connection.php';

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if email is provided in the JSON input
if (!isset($input['email'])) {
    echo json_encode(['error' => 'Email is required']);
    exit();
}

$email = $input['email'];

function getStudentTimetable($email) {
    global $pdo;

    // Get the student data by email to fetch their class_id
    $studentSql = "SELECT s.class_id
                   FROM Student s
                   WHERE s.email = :email";
    $stmt = $pdo->prepare($studentSql);
    $stmt->execute(['email' => $email]);
    $student = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$student) {
        return null; // Student not found
    }

    $classId = $student['class_id'];

    // Now retrieve the timetable based on class_id
    $sql = "SELECT t.day_of_week, t.start_time, t.end_time, sub.title AS subject, 
                   CONCAT(tchr.fname, ' ', tchr.lname) AS teacher_name
            FROM TimeTable t
            JOIN Teacher tchr ON t.teacher_id = tchr.id
            JOIN Subject sub ON t.subject_id = sub.id
            WHERE t.class_id = :class_id
            ORDER BY FIELD(t.day_of_week, 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday'), t.start_time";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['class_id' => $classId]);

    $timetable = $stmt->fetchAll(PDO::FETCH_ASSOC);

    return $timetable;
}

// Fetch timetable
$timetable = getStudentTimetable($email);

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
            'teacher' => $entry['teacher_name']
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
