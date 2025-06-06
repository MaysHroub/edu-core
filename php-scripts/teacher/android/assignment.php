<?php
// assignment.php - Create assignments
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    
    $title = $data['title'];
    $subject_id = $data['subject_id'];
    $class_id = $data['class_id'];
    $teacher_id = $data['teacher_id'];
    $max_score = $data['max_score'];
    $deadline = $data['deadline'];
    $question_file_url = $data['question_file_url'];
    
    // Insert into Task table
    $stmt = $conn->prepare("INSERT INTO Task (type, subject_id, class_id, teacher_id, max_score, title) VALUES ('assignment', ?, ?, ?, ?, ?)");
    $stmt->bind_param("iiiis", $subject_id, $class_id, $teacher_id, $max_score, $title);
    
    if ($stmt->execute()) {
        $task_id = $conn->insert_id;
        
        // Insert into Assignment table
        $stmt2 = $conn->prepare("INSERT INTO Assignment (id, question_file_url, deadline) VALUES (?, ?, ?)");
        $stmt2->bind_param("iss", $task_id, $question_file_url, $deadline);
        
        if ($stmt2->execute()) {
            echo json_encode(['success' => true]);
        } else {
            echo json_encode(['error' => 'Failed to create assignment']);
        }
    } else {
        echo json_encode(['error' => 'Failed to create task']);
    }
}
?>

<?php
// exams.php - Get exams by subject and class
header('Content-Type: application/json');
require_once 'db_connect.php';

$subject_id = $_GET['subject_id'] ?? 0;
$class_id = $_GET['class_id'] ?? 0;

$stmt = $conn->prepare("SELECT id, title FROM Task WHERE subject_id = ? AND class_id = ? AND type = 'exam'");
$stmt->bind_param("ii", $subject_id, $class_id);
$stmt->execute();
$result = $stmt->get_result();

$exams = [];
while ($row = $result->fetch_assoc()) {
    $exams[] = $row;
}

echo json_encode($exams);
?>

<?php
// students.php - Get students by class
header('Content-Type: application/json');
require_once 'db_connect.php';

$class_id = $_GET['class_id'] ?? 0;

$stmt = $conn->prepare("SELECT id, name FROM Student WHERE class_id = ?");
$stmt->bind_param("i", $class_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode($students);
?>

<?php
// publish_marks.php - Publish marks for students
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    $marks = $data['marks'];
    
    $success = true;
    
    foreach ($marks as $mark) {
        $task_id = $mark['task_id'];
        $student_id = $mark['student_id'];
        $mark_value = $mark['mark'];
        
        $stmt = $conn->prepare("INSERT INTO TaskResult (task_id, student_id, mark) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE mark = ?");
        $stmt->bind_param("isdd", $task_id, $student_id, $mark_value, $mark_value);
        
        if (!$stmt->execute()) {
            $success = false;
            break;
        }
    }
    
    if ($success) {
        echo json_encode(['success' => true]);
    } else {
        echo json_encode(['error' => 'Failed to publish marks']);
    }
}
?>