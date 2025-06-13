<?php
header('Content-Type: application/json');
require_once 'config/database.php';

// Get POST data
$data = json_decode(file_get_contents('php://input'), true);
$email = isset($data['email']) ? $data['email'] : '';

if (empty($email)) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Email is required'
    ]);
    exit;
}

try {
    // Get student data
    $query = "SELECT s.*, g.grade_number, c.section 
              FROM students s 
              LEFT JOIN grades g ON s.grade_id = g.id 
              LEFT JOIN classrooms c ON s.classroom_id = c.id 
              WHERE s.email = ?";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $student = $result->fetch_assoc();
        echo json_encode([
            'status' => 'success',
            'student' => $student
        ]);
    } else {
        echo json_encode([
            'status' => 'error',
            'message' => 'Student not found'
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