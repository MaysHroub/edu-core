<?php
// get_class_students.php
header('Content-Type: application/json'); 
require_once 'connection.php';  

$class_id = $_GET['classId'] ?? null; 

if (!$class_id) {     
    echo json_encode([]);     
    exit; 
}  

try {
    $sql = "SELECT id, name, email, grade_number, class_id FROM Student WHERE class_id = ?"; 
    $stmt = $pdo->prepare($sql); 
    $stmt->execute([$class_id]); 
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $out = []; 
    foreach ($results as $row) {     
        $out[] = [         
            'id'           => $row['id'],
            'name'         => $row['name'],         
            'email'        => $row['email'],         
            'grade_number' => $row['grade_number'],         
            'class_id'     => $row['class_id']     
        ]; 
    }  

    echo json_encode($out); 
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>

<?php
