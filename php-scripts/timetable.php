<?php
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['error' => 'Only GET method allowed']);
    exit;
}

try {
    $teacher_id = filter_input(INPUT_GET, 'teacher_id', FILTER_VALIDATE_INT);
    $detailed = filter_input(INPUT_GET, 'detailed', FILTER_VALIDATE_BOOLEAN) ?? false;
    
    if (!$teacher_id) {
        throw new Exception("teacher_id is required");
    }
    
    if ($detailed) {
        // Detailed timetable with all information
        $sql = "
        SELECT 
            tt.subject_id,
            s.title AS subject_title,
            s.grade_number,
            tt.class_id,
            tt.day_of_week,
            TIME_FORMAT(tt.start_time, '%H:%i') as start_time,
            TIME_FORMAT(tt.end_time, '%H:%i') as end_time
        FROM TimeTable tt
        JOIN Subject s ON tt.subject_id = s.id
        WHERE tt.teacher_id = ?
        ORDER BY 
            FIELD(tt.day_of_week, 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday'),
            tt.start_time
        ";
    } else {
        // Simple timetable with just subjects and classes
        $sql = "
        SELECT DISTINCT
            s.id AS subject_id,
            s.title AS subject_title,
            c.id AS class_id,
            cg.grade_number
        FROM TimeTable tt
        JOIN Subject s ON tt.subject_id = s.id
        JOIN Classroom c ON tt.class_id = c.id
        JOIN ClassGrade cg ON cg.grade_number = s.grade_number
        WHERE tt.teacher_id = ?
        ORDER BY s.grade_number, s.title
        ";
    }
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception("Query preparation failed: " . $conn->error);
    }
    
    $stmt->bind_param('i', $teacher_id);
    
    if (!$stmt->execute()) {
        throw new Exception("Query execution failed: " . $stmt->error);
    }
    
    $result = $stmt->get_result();
    
    if ($detailed) {
        $timetable = [];
        while ($row = $result->fetch_assoc()) {
            $timetable[] = [
                'subject_id' => (int)$row['subject_id'],
                'subject_title' => $row['subject_title'],
                'grade_number' => (int)$row['grade_number'],
                'class_id' => (int)$row['class_id'],
                'day_of_week' => $row['day_of_week'],
                'start_time' => $row['start_time'],
                'end_time' => $row['end_time']
            ];
        }
    } else {
        $subjects = [];
        $classes = [];
        
        while ($row = $result->fetch_assoc()) {
            $subjects[$row['subject_id']] = $row['subject_title'];
            $classes[$row['class_id']] = $row['grade_number'];
        }
        
        $timetable = [
            "subjects" => array_map(function ($id, $title) {
                return ["id" => (int)$id, "title" => $title];
            }, array_keys($subjects), $subjects),
            
            "classes" => array_map(function ($id, $grade) {
                return ["id" => (int)$id, "grade_number" => (int)$grade];
            }, array_keys($classes), $classes)
        ];
    }
    
    echo json_encode($timetable);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => $e->getMessage()]);
} finally {
    if (isset($stmt)) {
        $stmt->close();
    }
    if (isset($conn)) {
        $conn->close();
    }
}
?> 