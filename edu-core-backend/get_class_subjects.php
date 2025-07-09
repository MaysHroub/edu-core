<?php
require_once 'connection.php';  // Include the database connection

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if grade_number is provided in the JSON input
if (!isset($input['grade_number'])) {
    echo json_encode(['error' => 'Grade number is required']);
    exit();
}

$grade_number = $input['grade_number'];

/**
 * Fetch all subjects for a specific class.
 *
 * @param int $grade_number The grade number to fetch subjects for.
 * @return array List of subjects for the class.
 */
function getClassSubjects($grade_number) {
    global $pdo;
    
    $query = "SELECT s.id, s.title FROM Subject s
              JOIN ClassGrade cg ON s.grade_number = cg.grade_number
              WHERE cg.grade_number = :grade_number";

    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':grade_number', $grade_number);
    $stmt->execute();

    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Fetch the subjects
$subjects = getClassSubjects($grade_number);

// Return the result as JSON
echo json_encode(['status' => 'success', 'subjects' => $subjects]);
?>
