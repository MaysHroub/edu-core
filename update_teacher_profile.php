<?php
header('Content-Type: application/json');
require_once 'db_connection.php';

// Get the data from POST request
$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'];
$fname = $data['fname'];
$lname = $data['lname'];
$phone_number = $data['phone_number'];
$date_of_birth = $data['date_of_birth'];

try {
    // Prepare the SQL query to update teacher information
    $query = "UPDATE Teacher 
              SET fname = ?, lname = ?, phone_number = ?, date_of_birth = ? 
              WHERE email = ?";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("sssss", $fname, $lname, $phone_number, $date_of_birth, $email);
    
    if ($stmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Profile updated successfully'
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Failed to update profile'
        ]);
    }
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}

$stmt->close();
$conn->close();
?> 