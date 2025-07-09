<?php
header('Content-Type: application/json');
require_once 'connection.php';

try {
    $stmt = $pdo->query("
        SELECT Teacher.*, Authentication.password
        FROM Teacher
        JOIN Authentication ON Teacher.email = Authentication.email
    ");
    $teachers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($teachers);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>