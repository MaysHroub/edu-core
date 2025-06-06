<?php
// students.php - Fetch students for a specific class
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $class_id = $_GET['class_id'] ?? null;
    
    if (!$class_id) {
        echo json_encode(['error' => 'Missing class_id']);
        exit;
    }
    
    try {
        $stmt = $conn->prepare("
            SELECT s.id, s.name, s.email, s.grade_number, s.date_of_birth
            FROM Student s 
            WHERE s.class_id = ?
            ORDER BY s.name ASC
        ");
        
        $stmt->bind_param("i", $class_id);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $students = [];
        while ($row = $result->fetch_assoc()) {
            $students[] = [
                'id' => $row['id'],
                'name' => $row['name'],
                'email' => $row['email'],
                'grade_number' => (int)$row['grade_number'],
                'date_of_birth' => $row['date_of_birth']
            ];
        }
        
        echo json_encode($students);
        
    } catch (Exception $e) {
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
} else {
    echo json_encode(['error' => 'Only GET method allowed']);
}
?>