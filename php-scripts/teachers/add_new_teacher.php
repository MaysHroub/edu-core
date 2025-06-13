<?php
header('Content-Type: application/json');
require_once '../connection.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Only POST method allowed']);
    exit();
}

$input = file_get_contents('php://input');
$data = json_decode($input, true);

$required = ['fname', 'lname', 'email', 'password', 'phone_number', 'date_of_birth', 'subject_id'];
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
    $stmtAuth->execute([$data['email'], $data['password'], 'teacher']);

    $stmt = $pdo->prepare(
        "INSERT INTO Teacher (fname, lname, email, phone_number, date_of_birth, subject_id)
         VALUES (?, ?, ?, ?, ?, ?)"
    );
    $stmt->execute([
        $data['fname'],
        $data['lname'],
        $data['email'],
        $data['phone_number'],
        $data['date_of_birth'],
        $data['subject_id']
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
