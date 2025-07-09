<?php
header('Content-Type: application/json');
require_once 'connection.php';

// Check if 'id' is provided in the URL parameters
if (!isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Missing teacher ID']);
    exit();
}

$id = $_GET['id'];

try {
    // Begin transaction
    $pdo->beginTransaction();

    // Get the teacher's email
    $stmtEmail = $pdo->prepare("SELECT email FROM Teacher WHERE id = ?");
    $stmtEmail->execute([$id]);
    $teacher = $stmtEmail->fetch(PDO::FETCH_ASSOC);

    if (!$teacher) {
        http_response_code(404);
        echo json_encode(['error' => 'Teacher not found']);
        exit();
    }

    $email = $teacher['email'];

    // Delete the teacher
    $stmtDeleteTeacher = $pdo->prepare("DELETE FROM Teacher WHERE id = ?");
    $stmtDeleteTeacher->execute([$id]);

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
