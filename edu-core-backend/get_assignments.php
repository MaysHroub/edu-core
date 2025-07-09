<?php
header("Content-Type: application/json");
require_once("connection.php");

if (isset($_GET['student_id'])) {
    $studentId = $_GET['student_id'];

    // get the class_id of the student
    $stmt = $pdo->prepare("SELECT class_id FROM Student WHERE id = ?");
    $stmt->execute([$studentId]);
    $classId = $stmt->fetchColumn();

    if (!$classId) {
        echo json_encode(["error" => "Invalid student ID"]);
        exit;
    }

    // selects all assignments that belong to the std's class, have a deadline, and not submitted yet.
    // also returns results sorted by soonest deadline first
    $sql = "
        SELECT 
            A.id,
            S.title AS title,
            A.deadline
        FROM Assignment A
        JOIN Task T ON A.id = T.id
        JOIN Subject S ON T.subject_id = S.id
        WHERE T.class_id = ?
          AND A.deadline >= CURDATE()
          AND NOT EXISTS (
              SELECT 1 FROM AssignmentSubmission AS ASB
              WHERE ASB.id = A.id AND ASB.student_id = ?
          )
        ORDER BY A.deadline ASC
    ";
    /*  [
            ["id" => 4, "title" => "Math", "deadline" => "2025-06-25"],
            ["id" => 5, "title" => "Science", "deadline" => "2025-06-30"]
        ]   
             */

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$classId, $studentId]);
    $assignments = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($assignments);
} else {
    echo json_encode(["error" => "student_id missing"]);
}