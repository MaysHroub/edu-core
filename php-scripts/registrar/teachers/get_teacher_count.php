<?php
header('Content-Type: application/json');
require_once '../connection.php';

try {
    $sql = "SELECT COUNT(*) AS count FROM Teacher";
    $stmt = $pdo->prepare($sql);   
    $stmt->execute();
    $row = $stmt->fetch(PDO::FETCH_ASSOC);  
    echo json_encode($row);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>