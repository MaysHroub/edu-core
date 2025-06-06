<?php
// tasks.php - Fetch tasks (assignments/exams) for a specific subject and class
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $subject_id = $_GET['subject_id'] ?? null;
    $class_id = $_GET['class_id'] ?? null;
    
    if (!$subject_id || !$class_id) {
        echo json_encode(['error' => 'Missing subject_id or class_id']);
        exit;
    }
    
    try {
        $stmt = $conn->prepare("
            SELECT t.id, t.type, t.date, t.max_score,
                   CASE 
                       WHEN a.id IS NOT NULL THEN CONCAT(tt.title, ' - ', DATE_FORMAT(t.date, '%Y-%m-%d'))
                       ELSE CONCAT(tt.title, ' - ', DATE_FORMAT(t.date, '%Y-%m-%d'))
                   END as title,
                   CASE 
                       WHEN a.id IS NOT NULL THEN 1
                       ELSE 0
                   END as is_assignment
            FROM Task t 
            JOIN TaskType tt ON t.type = tt.title
            LEFT JOIN Assignment a ON t.id = a.id
            WHERE t.subject_id = ? AND t.class_id = ?
            ORDER BY t.date DESC
        ");
        
        $stmt->bind_param("ii", $subject_id, $class_id);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $tasks = [];
        while ($row = $result->fetch_assoc()) {
            $tasks[] = [
                'id' => (int)$row['id'],
                'title' => $row['title'],
                'type' => $row['type'],
                'date' => $row['date'],
                'max_score' => (float)$row['max_score'],
                'is_assignment' => (bool)$row['is_assignment']
            ];
        }
        
        echo json_encode($tasks);
        
    } catch (Exception $e) {
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
}
?>