<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Get the teacher's email from the request
$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';

if (empty($email)) {
    echo json_encode(['success' => false, 'message' => 'Email is required']);
    exit;
}

try {
    // Get teacher data with subject name
    $query = "SELECT t.*, s.title as subject_title 
              FROM teacher t 
              LEFT JOIN subject s ON t.subject_id = s.id 
              WHERE t.email = ?";
    
    $stmt = $conn->prepare($query);
    $stmt->execute([$email]);
    $teacher = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($teacher) {
        // Format the date of birth
        $teacher['date_of_birth'] = date('Y-m-d', strtotime($teacher['date_of_birth']));
        
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
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?> 