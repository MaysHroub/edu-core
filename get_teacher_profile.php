<?php
header('Content-Type: application/json');
require_once 'db_connection.php';

// Get the email from POST request
$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'];

try {
    // Prepare the SQL query to get teacher information with subject details
    $query = "SELECT t.*, s.title as subject_title 
              FROM Teacher t 
              LEFT JOIN Subject s ON t.subject_id = s.id 
              WHERE t.email = ?";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $teacher = $result->fetch_assoc();
        echo json_encode([
            'success' => true,
            'teacher' => $teacher
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Teacher not found'
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