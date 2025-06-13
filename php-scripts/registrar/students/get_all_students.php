<?php
header('Content-Type: application/json');
require_once '../connection.php';

try {
    $stmt = $pdo->query("
        SELECT Student.*, Authentication.password
        FROM Student
        JOIN Authentication ON Student.email = Authentication.email
    ");
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($students);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>