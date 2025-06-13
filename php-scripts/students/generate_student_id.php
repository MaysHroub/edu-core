<?php
header('Content-Type: application/json');
require_once 'connection.php';

try {
    // Get the maximum current student ID
    $stmt = $pdo->query("SELECT MAX(id) AS max_id FROM Student");
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    // If no student exist, start from 2025001
    $nextId = ($row && $row['max_id']) ? ((int)$row['max_id'] + 1) : 2025001;

    echo json_encode(['next_student_id' => $nextId]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>