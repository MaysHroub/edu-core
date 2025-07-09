<?php
header('Content-Type: application/json');
require_once 'connection.php';

$sql = "SELECT COUNT(*) AS count FROM Subject";
$stmt = $pdo->prepare($sql);   
$stmt->execute();

$row = $stmt->fetch(PDO::FETCH_ASSOC);  
echo json_encode($row);
?>
