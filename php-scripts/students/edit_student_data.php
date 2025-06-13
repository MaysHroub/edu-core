<?php
// Include the database connection file
header('Content-Type: application/json');
require_once 'connection.php';

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if necessary fields are provided in the JSON input
if (!isset($input['email']) || !isset($input['fname']) || !isset($input['lname']) || !isset($input['date_of_birth']) || !isset($input['class_id'])) {
    echo json_encode(['error' => 'All fields are required']);
    exit();
}

$email = $input['email'];
$fname = $input['fname'];
$lname = $input['lname'];
$date_of_birth = $input['date_of_birth'];
$class_id = $input['class_id'];

function editStudentData($email, $fname, $lname, $date_of_birth, $class_id) {
    global $pdo;

    // Update the student data
    $sql = "UPDATE Student
            SET fname = :fname, lname = :lname, date_of_birth = :date_of_birth, class_id = :class_id
            WHERE email = :email";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'email' => $email,
        'fname' => $fname,
        'lname' => $lname,
        'date_of_birth' => $date_of_birth,
        'class_id' => $class_id
    ]);

    if ($stmt->rowCount()) {
        return ['status' => 'success', 'message' => 'Student data updated successfully.'];
    } else {
        return ['status' => 'error', 'message' => 'No changes made or student not found.'];
    }
}

// Edit student data
$response = editStudentData($email, $fname, $lname, $date_of_birth, $class_id);

echo json_encode($response);
?>
