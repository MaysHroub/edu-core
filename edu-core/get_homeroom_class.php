<?php
header('Content-Type: application/json');
require_once 'connection.php'; // assumes the connection code is saved as 'connection.php'

if (!isset($_GET['teacher_id'])) {
    http_response_code(400); // Bad request
    echo json_encode(['error' => 'Missing teacher_id']);
    exit;
}

$teacherId = intval($_GET['teacher_id']);

try {
    $stmt = $pdo->prepare("SELECT id AS class_id FROM Classroom WHERE homeroom_teacher_id = ?");
    $stmt->execute([$teacherId]);
    $class = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($class) {
        echo json_encode(['class_id' => $class['class_id']]);
    } else {
        echo json_encode(['class_id' => null]); // no class found
    }
} catch (PDOException $e) {
    http_response_code(500); // Internal Server Error
    echo json_encode(['error' => 'Database query failed: ' . $e->getMessage()]);
}
?>
