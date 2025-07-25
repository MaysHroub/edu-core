<?php
header('Content-Type: application/json');
require_once 'connection.php';

try {
    $stmt = $pdo->query("SELECT * FROM Classroom");
    $classrooms = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($classrooms);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>