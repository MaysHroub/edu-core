<?php
// get_class_students.php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

$class_id = $_GET['classId'] ?? null;

if (!$class_id) {
    echo json_encode(['error' => 'Missing classId parameter']);
    exit;
}

try {
    // Updated SQL query to match your actual database schema
    $sql = "
        SELECT 
            s.id,
            s.fname,
            s.lname,
            CONCAT(s.fname, ' ', s.lname) AS name,
            s.email,
            s.class_id,
            c.grade_number
        FROM Student s
        INNER JOIN Classroom c ON s.class_id = c.id
        WHERE s.class_id = ?
        ORDER BY s.lname, s.fname
    ";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$class_id]);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $out = [];
    foreach ($results as $row) {
        $out[] = [
            'id'           => $row['id'],
            'name'         => $row['name'],
            'fname'        => $row['fname'],
            'lname'        => $row['lname'],
            'email'        => $row['email'],
            'grade_number' => $row['grade_number'],
            'class_id'     => $row['class_id']
        ];
    }

    echo json_encode($out);
    
} catch (PDOException $e) {
    error_log("Database error in get_class_students.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>