<?php
// submit_absence.php
header('Content-Type: application/json');
require_once 'connection.php';

$input = json_decode(file_get_contents('php://input'), true);

$teacher_id = $input['teacher_id'] ?? null;
$class_id   = $input['class_id']   ?? null;
$absences   = $input['absences']   ?? [];

if (!$teacher_id || !$class_id || !is_array($absences)) {
    http_response_code(400);
    echo json_encode(['error'=>'Invalid input']);
    exit;
}

try {
    $pdo->beginTransaction();

    // delete existing for that date/class
    $date = $absences[0]['date'] ?? date('Y-m-d');
    $del = $pdo->prepare(
      "DELETE a
         FROM Absence a
         JOIN Student s ON a.student_id = s.id
        WHERE s.class_id = ? AND a.date = ?"
    );
    $del->execute([$class_id, $date]);

    // insert new
    $ins = $pdo->prepare(
      "INSERT INTO Absence (student_id, date, status)
       VALUES (?, ?, ?)"
    );
    foreach ($absences as $rec) {
        $ins->execute([
            $rec['student_id'],
            $rec['date'],
            $rec['status']
        ]);
    }

    $pdo->commit();
    echo json_encode(['success'=>true,'message'=>'Attendance saved']);
} catch (Exception $e) {
    $pdo->rollback();
    http_response_code(500);
    echo json_encode(['error'=>$e->getMessage()]);
}
?>
