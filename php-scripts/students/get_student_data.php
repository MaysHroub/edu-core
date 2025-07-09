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

function getStudentDataByEmail($email) {
    global $pdo;

    $sql = "SELECT s.id, s.fname, s.lname, s.email, s.date_of_birth, c.grade_number, c.section
            FROM Student s
            JOIN Classroom c ON s.class_id = c.id
            WHERE s.email = :email";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['email' => $email]);

    $student = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($student) {
        return $student;
    } else {
        return null; // Student not found
    }
}

// Fetch student data
$student = getStudentDataByEmail($email);

if ($student) {
    echo json_encode(['status' => 'success', 'student' => $student]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Student not found']);
}
?>
