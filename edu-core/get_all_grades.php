<?php
header('Content-Type: application/json');
require_once 'connection.php';

try {
    $stmt = $pdo->query("SELECT * FROM ClassGrade");
    $grades = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($grades);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>