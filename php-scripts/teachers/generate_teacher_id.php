<?php
header('Content-Type: application/json');
require_once 'connection.php';

try {
    // Get the maximum current teacher ID
    $stmt = $pdo->query("SELECT MAX(id) AS max_id FROM Teacher");
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    // If no teachers exist, start from 125001
    $nextId = ($row && $row['max_id']) ? ((int)$row['max_id'] + 1) : 125001;

    echo json_encode(['next_teacher_id' => $nextId]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>