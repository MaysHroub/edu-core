<?php
// get_subjects.php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

try {
    $sql = "SELECT DISTINCT 
                s.id,
                s.title,
                s.grade_number,
                s.semester_number
            FROM Subject s
            ORDER BY s.grade_number ASC, s.title ASC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $subjects = [];
    foreach ($results as $row) {
        $subjects[] = [
            'id' => (int)$row['id'],
            'title' => $row['title'],
            'grade_number' => (int)$row['grade_number'],
            'semester_number' => (int)$row['semester_number']
        ];
    }

    echo json_encode($subjects);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error',
        'message' => 'Failed to retrieve subjects: ' . $e->getMessage()
    ]);
    error_log("Database error in get_subjects.php: " . $e->getMessage());
}
?>
