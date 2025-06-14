<?php
header("Content-Type: application/json");
require_once("connection.php"); 

if (isset($_GET['student_id'])) {
    $studentId = $_GET['student_id'];

    // Get the class_id of the student
    $stmt = $pdo->prepare("SELECT class_id FROM student WHERE id = ?");
    $stmt->execute([$studentId]);
    $classId = $stmt->fetchColumn();

    if (!$classId) {
        echo json_encode(["error" => "Invalid student ID"]);
        exit;
    }

    // Main query to return both assignments and exams/events
    $sql = "
        SELECT 
            S.title AS subject_title,
            T.type,
            T.date,
            A.deadline,
            A.question_file_url
        FROM task T
        JOIN subject S ON T.subject_id = S.id
        LEFT JOIN assignment A ON T.id = A.id
        WHERE T.class_id = ?
          AND (
              (LOWER(T.type) = 'assignment' AND A.deadline >= CURDATE()) OR 
              (LOWER(T.type) <> 'assignment' AND T.date >= CURDATE())
          )
        ORDER BY COALESCE(A.deadline, T.date) ASC
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$classId]);
    $events = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($events);
}
?>
