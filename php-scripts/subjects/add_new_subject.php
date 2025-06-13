<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Only POST method allowed']);
    exit();
}

// Read the raw input
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Check for required fields
$required = ['title', 'description', 'grade_number', 'semester_number'];
foreach ($required as $field) {
    if (!isset($data[$field])) {
        http_response_code(400);
        echo json_encode(['error' => "Missing field: $field"]);
        exit();
    }
}

try {
    $stmt = $pdo->prepare(
        "INSERT INTO Subject (title, description, grade_number, semester_number)
         VALUES (?, ?, ?, ?)"
    );
    $result = $stmt->execute([
        $data['title'],
        $data['description'],
        $data['grade_number'],
        $data['semester_number']
    ]);

    if ($result) {
        echo json_encode(['success' => true]);
    } else {
        http_response_code(500);
        echo json_encode(['error' => 'Insert failed']);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}   
?>