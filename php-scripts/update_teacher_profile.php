<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Get the request data
$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';
$fname = $data['fname'] ?? '';
$lname = $data['lname'] ?? '';
$phone = $data['phone_number'] ?? '';
$dob = $data['date_of_birth'] ?? '';

if (empty($email) || empty($fname) || empty($lname) || empty($phone) || empty($dob)) {
    echo json_encode(['success' => false, 'message' => 'All fields are required']);
    exit;
}

try {
    // Update teacher data
    $query = "UPDATE teacher 
              SET fname = ?, lname = ?, phone_number = ?, date_of_birth = ? 
              WHERE email = ?";
    
    $stmt = $conn->prepare($query);
    $result = $stmt->execute([$fname, $lname, $phone, $dob, $email]);

    if ($result) {
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
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 