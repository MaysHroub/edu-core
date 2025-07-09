<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Only allow PUT requests
if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
    http_response_code(405);
    echo json_encode(['error' => 'Only PUT method allowed']);
    exit();
}

// Read the raw input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Check for required fields
$required = ['id', 'title', 'description', 'grade_number', 'semester_number'];
foreach ($required as $field) {
    if (!isset($data[$field])) {
        http_response_code(400);
        echo json_encode(['error' => "Missing field: $field"]);
        exit();
    }
}

try {
    $stmt = $pdo->prepare(
        "UPDATE Subject SET title=?, description=?, grade_number=?, semester_number=? WHERE id=?"
    );
    $result = $stmt->execute([
        $data['title'],
        $data['description'],
        $data['grade_number'],
        $data['semester_number'],
        $data['id']
    ]);

    if ($result) {
        echo json_encode(['success' => true]);
    } else {
        http_response_code(500);
        echo json_encode(['error' => 'Update failed']);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>