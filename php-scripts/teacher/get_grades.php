<?php
// get_grades.php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

try {
    $sql = "SELECT grade_number FROM ClassGrade ORDER BY grade_number ASC";
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $grades = [];
    foreach ($results as $row) {
        $grades[] = [
            'grade_number' => (int)$row['grade_number']
        ];
    }

    echo json_encode($grades);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error',
        'message' => 'Failed to retrieve grades: ' . $e->getMessage()
    ]);
    error_log("Database error in get_grades.php: " . $e->getMessage());
}
?>
