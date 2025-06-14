<?php
header("Content-Type: application/json"); // this is the response back to the app telling it it's gonna get json data
require_once("connection.php"); // use this method in every php file you create to connect to the database

if (isset($_GET['student_id'])) { // checks which student (using their id) wants to see their marks 
    $studentId = $_GET['student_id'];

    // gets subject title, task type, it's mark, max score that one can get on this task, date and feedback
    // joins three tables: Subject for it's title, task for the data about it and TaskResult for the marks the student got on the task
    $sql = "
        SELECT 
            S.title AS subject_title,
            T.type,
            TR.mark,
            T.max_score,
            T.date,
            TR.feedback
        FROM TaskResult TR
        JOIN Task T ON TR.task_id = T.id
        JOIN Subject S ON T.subject_id = S.id
        WHERE TR.student_id = ?
    ";

    $stmt = $pdo->prepare($sql); // for sql injection
    $stmt->execute([$studentId]); // executes the query passing the id in place of (?) in the sql query
    // this right here yall fetches all matching rows as an associative array (making it look like a JSON structure)
    $marks = $stmt->fetchAll(PDO::FETCH_ASSOC);
    /*
      [
        "subject_title" => "Math",
        "type" => "exam",
        "mark" => 9.5,
        "max_score" => 10,
        "date" => "2025-05-22",
        "feedback" => "Excellent!"
      ]
    */

    echo json_encode($marks); // converts it to JSON in the structure GSON expects (because it's in associative array form)
}
?>
