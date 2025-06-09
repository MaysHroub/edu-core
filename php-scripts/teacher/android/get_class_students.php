<?php 
header('Content-Type: application/json'); 
require_once 'db_connect.php';  

$class_id = $_GET['classId'] ?? null; 

if (!$class_id) {     
    echo json_encode([]);     
    exit; 
}  

// Fixed SQL query - using correct column name from schema
$sql = "SELECT id, name, email, grade_number, class_id FROM Student WHERE class_id = ?"; 
$stmt = $conn->prepare($sql); 
$stmt->bind_param('i', $class_id); 
$stmt->execute(); 
$res = $stmt->get_result();  

$out = []; 
while ($row = $res->fetch_assoc()) {     
    $out[] = [         
        'id'           => $row['id'],  // Keep as string since it's VARCHAR(20)
        'name'         => $row['name'],         
        'email'        => $row['email'],         
        'grade_number' => $row['grade_number'],         
        'class_id'     => $row['class_id']     
    ]; 
}  

echo json_encode($out); 
$stmt->close(); 
$conn->close(); 
?>