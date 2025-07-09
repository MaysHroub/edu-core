<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Check if 'id' is provided in the URL parameters
if (!isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Missing subject ID']);
    exit();
}

$id = $_GET['id'];

try {
    $stmt = $pdo->prepare("DELETE FROM Subject WHERE id = ?");
    $result = $stmt->execute([$id]);

    if ($result && $stmt->rowCount() > 0) {
        echo json_encode(['success' => true]);
    } else {
        http_response_code(404);
        echo json_encode(['error' => 'Subject not found or already deleted']);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>