<?php
header("Content-Type: application/json");
require_once("../includes/connection.php"); 

if (isset($_GET['student_id'])) {
    $studentId = $_GET['student_id'];
    // finding the class the student belongs to
    $stmt = $pdo->prepare("SELECT class_id FROM Student WHERE id = ?"); 
    $stmt->execute([$studentId]);
    $classId = $stmt->fetchColumn(); // returns just the first value (class ID) from the first row

    if (!$classId) { // if it doesn't exist 
        echo json_encode(["error" => "Invalid student ID"]);
        exit;
    }

    // joins subject table to get it's title and assignment table to get related info on it
    // filters are to make sure that the deadlines and exams are upcoming and not expired
    $sql = "
        SELECT 
            S.title AS subject_title,
            T.type,
            T.date,
            A.deadline,
            A.question_file_url
        FROM Task T
        JOIN Subject S ON T.subject_id = S.id
        LEFT JOIN Assignment A ON T.id = A.id
        WHERE T.class_id = ?
          AND (
              (T.type = 'assignment' AND A.deadline >= CURDATE()) OR 
              (T.type <> 'assignment' AND T.date >= CURDATE())
          )
        ORDER BY COALESCE(A.deadline, T.date) ASC
    ";
    /*   [
            [
                "subject_title" => "Math",
                "type" => "assignment",
                "date" => "2025-06-20",
                "deadline" => "2025-06-25",
                "question_file_url" => "math_homework.pdf"
            ],
            ...
        ]

    */

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$classId]);
    $events = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($events);
}
?>
