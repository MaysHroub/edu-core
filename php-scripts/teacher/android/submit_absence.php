<?php
header('Content-Type: application/json');
require_once 'db_connect.php';

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
    $conn->begin_transaction();

    // delete existing for that date/class
    $date = $absences[0]['date'] ?? date('Y-m-d');
    $del = $conn->prepare(
      "DELETE a
         FROM Absence a
         JOIN Student s ON a.student_id = s.id
        WHERE s.class_id = ? AND a.date = ?"
    );
    $del->bind_param('is', $class_id, $date);
    $del->execute();

    // insert new
    $ins = $conn->prepare(
      "INSERT INTO Absence (student_id, date, status)
       VALUES (?, ?, ?)"
    );
    foreach ($absences as $rec) {
        $ins->bind_param('sss',
            $rec['student_id'],
            $rec['date'],
            $rec['status']
        );
        $ins->execute();
    }

    $conn->commit();
    echo json_encode(['success'=>true,'message'=>'Attendance saved']);
} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode(['error'=>$e->getMessage()]);
}