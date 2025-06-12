<?php
require_once 'connection.php';

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
    exit;
}

// Get teacher_id from query parameters
$teacherId = isset($_GET['teacher_id']) ? intval($_GET['teacher_id']) : 0;

if ($teacherId <= 0) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid teacher ID']);
    exit;
}

try {
    // Get the class where this teacher is the homeroom teacher
    $stmt = $pdo->prepare("
        SELECT 
            c.id as class_id,
            COUNT(s.id) as student_count
        FROM Classroom c
        LEFT JOIN Student s ON c.id = s.class_id
        WHERE c.homeroom_teacher_id = ?
        GROUP BY c.id
    ");
    
    $stmt->execute([$teacherId]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($result) {
        echo json_encode([
            'success' => true,
            'class_id' => $result['class_id'],
            'student_count' => $result['student_count']
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'This teacher is not assigned as a homeroom teacher to any class'
        ]);
    }
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>