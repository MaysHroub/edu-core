<?php
header('Content-Type: application/json');
require_once '../connection.php';

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
$required = ['fname', 'lname', 'email', 'password', 'class_id', 'date_of_birth'];
foreach ($required as $field) {
    if (!isset($data[$field])) {
        http_response_code(400);
        echo json_encode(['error' => "Missing field: $field"]);
        exit();
    }
}

try {
    $pdo->beginTransaction();

    $stmtAuth = $pdo->prepare(
        "INSERT INTO Authentication (email, password, user_type) VALUES (?, ?, ?)"
    );
    $stmtAuth->execute([$data['email'], $data['password'], 'student']);

    $stmt = $pdo->prepare(
        "INSERT INTO Student (fname, lname, email, date_of_birth, class_id)
         VALUES (?, ?, ?, ?, ?)"
    );
    $result = $stmt->execute([
        $data['fname'],
        $data['lname'],
        $data['email'],
        $data['date_of_birth'],
        $data['class_id']
    ]);

    $pdo->commit();
    echo json_encode(['success' => true]);

} catch (PDOException $e) {
    $pdo->rollBack();
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error: ' . $e->getMessage(),
        'trace' => $e->getTraceAsString()
    ]);
}
?>
