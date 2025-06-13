<?php
header('Content-Type: application/json');
require_once 'config/database.php';

// Get POST data
$data = json_decode(file_get_contents('php://input'), true);
$email = isset($data['email']) ? $data['email'] : '';
$fname = isset($data['fname']) ? $data['fname'] : '';
$lname = isset($data['lname']) ? $data['lname'] : '';
$dateOfBirth = isset($data['date_of_birth']) ? $data['date_of_birth'] : '';
$classId = isset($data['class_id']) ? $data['class_id'] : '';

if (empty($email) || empty($fname) || empty($lname) || empty($dateOfBirth) || empty($classId)) {
    echo json_encode([
        'status' => 'error',
        'message' => 'All fields are required'
    ]);
    exit;
}

try {
    // Update student data
    $query = "UPDATE students 
              SET fname = ?, 
                  lname = ?, 
                  date_of_birth = ?, 
                  classroom_id = ? 
              WHERE email = ?";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("sssis", $fname, $lname, $dateOfBirth, $classId, $email);
    
    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                'status' => 'success',
                'message' => 'Student data updated successfully'
            ]);
        } else {
            echo json_encode([
                'status' => 'error',
                'message' => 'No changes made or student not found'
            ]);
        }
    } else {
        echo json_encode([
            'status' => 'error',
            'message' => 'Failed to update student data'
        ]);
    }
} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}

$stmt->close();
$conn->close();
?> 