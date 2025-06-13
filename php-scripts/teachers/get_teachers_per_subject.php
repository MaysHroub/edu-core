<?php
header('Content-Type: application/json');
require_once '../connection.php';

$sql = "
    SELECT Subject.title AS title, COUNT(*) AS count 
    FROM Teacher
    JOIN Subject ON Teacher.subject_id = Subject.id
    GROUP BY Subject.id, Subject.title
";

$stmt = $pdo->prepare($sql);
$stmt->execute();

$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode($rows); 
?>
