<?php
header('Content-Type: application/json');
require_once '../connection.php';

$sql = "
    SELECT ClassGrade.grade_number AS grade, COUNT(*) AS count 
    FROM Student s
    JOIN Classroom c ON s.class_id = c.id
    JOIN ClassGrade ON c.grade_number = ClassGrade.grade_number
    GROUP BY ClassGrade.grade_number 
";

$stmt = $pdo->prepare($sql);
$stmt->execute();

$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode($rows); 
?>
