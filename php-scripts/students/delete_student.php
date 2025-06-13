<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Check if 'id' is provided in the URL parameters
if (!isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Missing student ID']);
    exit();
}

$id = $_GET['id'];

try {
    // Begin transaction
    $pdo->beginTransaction();

    // Get the email of the student
    $stmtEmail = $pdo->prepare("SELECT email FROM Student WHERE id = ?");
    $stmtEmail->execute([$id]);
    $student = $stmtEmail->fetch(PDO::FETCH_ASSOC);

    if (!$student) {
        http_response_code(404);
        echo json_encode(['error' => 'Student not found']);
        exit();
    }

    $email = $student['email'];

    // Delete the student
    $stmtDeleteStudent = $pdo->prepare("DELETE FROM Student WHERE id = ?");
    $stmtDeleteStudent->execute([$id]);

    // Delete from Authentication
    $stmtDeleteAuth = $pdo->prepare("DELETE FROM Authentication WHERE email = ?");
    $stmtDeleteAuth->execute([$email]);

    $pdo->commit();
    echo json_encode(['success' => true]);

} catch (PDOException $e) {
    $pdo->rollBack();
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>
